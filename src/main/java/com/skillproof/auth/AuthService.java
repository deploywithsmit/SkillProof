package com.skillproof.auth;

import com.skillproof.common.BadRequestException;
import com.skillproof.common.ConflictException;
import com.skillproof.common.ResourceNotFoundException;
import com.skillproof.common.UnauthorizedException;
import com.skillproof.profile.ProfileSettings;
import com.skillproof.profile.ProfileSettingsRepository;
import com.skillproof.security.SecurityUtils;
import com.skillproof.security.UserPrincipal;
import com.skillproof.user.Role;
import com.skillproof.user.User;
import com.skillproof.user.UserRepository;
import com.skillproof.user.UserStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final ProfileSettingsRepository profileSettingsRepository;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.auth.lockout-minutes:15}")
    private int lockoutMinutes;

    public AuthService(UserRepository userRepository,
                       ProfileSettingsRepository profileSettingsRepository,
                       OtpService otpService,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.profileSettingsRepository = profileSettingsRepository;
        this.otpService = otpService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new ConflictException("An account with this email address already exists.");
        }
        if (userRepository.existsByUsernameIgnoreCase(request.getUsername())) {
            throw new ConflictException("Username '" + request.getUsername() + "' is already taken.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User(
                request.getEmail().trim().toLowerCase(),
                request.getUsername().trim().toLowerCase(),
                request.getFullName().trim(),
                encodedPassword,
                Role.STUDENT,
                UserStatus.PENDING_VERIFICATION
        );

        // Initialize default profile settings via cascade
        ProfileSettings profile = new ProfileSettings();
        profile.setHeadline("Aspiring Software Engineer");
        user.setProfileSettings(profile);

        user = userRepository.save(user);

        // Dispatch email verification OTP
        otpService.generateAndSendOtp(user, OtpPurpose.EMAIL_VERIFICATION);

        logger.info("New student registered: {} ({}), pending email verification", user.getEmail(), user.getUsername());
        return AuthResponse.of("Account created. Please check your email for the 6-digit verification code.", "/auth.html?step=verify-email&email=" + user.getEmail());
    }

    @Transactional
    public AuthResponse verifyEmail(VerifyOtpRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with email: " + request.getEmail()));

        otpService.verifyAndConsumeOtp(user, request.getOtp(), OtpPurpose.EMAIL_VERIFICATION);

        user.setStatus(UserStatus.ACTIVE);
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);

        // Create authenticated session
        establishSession(user, httpRequest);

        logger.info("User {} successfully verified email and authenticated", user.getEmail());
        return AuthResponse.of("Email verified successfully! Welcome to SkillProof.", new UserSummaryDto(user), "/dashboard.html");
    }

    @Transactional
    public AuthResponse requestLoginOtp(LoginOtpRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No account registered with email: " + request.getEmail()));

        checkAccountStatus(user);

        otpService.generateAndSendOtp(user, OtpPurpose.LOGIN);
        logger.info("Login OTP dispatched for user {}", user.getEmail());
        return AuthResponse.of("A login code has been sent to your email address.", "/auth.html?step=verify-login&email=" + user.getEmail());
    }

    @Transactional
    public AuthResponse verifyLoginOtp(VerifyOtpRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with email: " + request.getEmail()));

        checkAccountStatus(user);

        otpService.verifyAndConsumeOtp(user, request.getOtp(), OtpPurpose.LOGIN);

        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);

        // Establish session
        establishSession(user, httpRequest);

        logger.info("User {} successfully authenticated via OTP", user.getEmail());
        return AuthResponse.of("Login successful. Welcome back!", new UserSummaryDto(user), "/dashboard.html");
    }

    @Transactional
    public AuthResponse passwordLogin(PasswordLoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password."));

        checkAccountStatus(user);

        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            handleFailedLoginAttempt(user);
            throw new UnauthorizedException("Invalid email or password.");
        }

        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);

        establishSession(user, httpRequest);

        logger.info("User {} authenticated with password", user.getEmail());
        return AuthResponse.of("Login successful.", new UserSummaryDto(user), "/dashboard.html");
    }

    @Transactional
    public void resendOtp(ResendOtpRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with email: " + request.getEmail()));

        checkAccountStatus(user);
        otpService.generateAndSendOtp(user, request.getPurpose());
    }

    @Transactional
    public AuthResponse forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmailIgnoreCase(request.getEmail()).ifPresent(user -> {
            if (user.getStatus() != UserStatus.SUSPENDED) {
                try {
                    otpService.generateAndSendOtp(user, OtpPurpose.PASSWORD_RESET);
                } catch (Exception ex) {
                    logger.warn("Could not dispatch password reset OTP: {}", ex.getMessage());
                }
            }
        });

        // Always return generic confirmation to protect privacy
        return AuthResponse.of("If an account exists with this email, a 6-digit reset code has been dispatched.", "/auth.html?step=reset-password&email=" + request.getEmail());
    }

    @Transactional
    public AuthResponse resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid reset request."));

        otpService.verifyAndConsumeOtp(user, request.getOtp(), OtpPurpose.PASSWORD_RESET);

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        if (user.getStatus() == UserStatus.LOCKED) {
            user.setStatus(UserStatus.ACTIVE);
        }
        userRepository.save(user);

        logger.info("Password successfully reset for user {}", user.getEmail());
        return AuthResponse.of("Password has been reset successfully. Please log in with your new credentials.", "/auth.html?step=login");
    }

    @Transactional(readOnly = true)
    public UserSummaryDto getCurrentUser() {
        UserPrincipal principal = SecurityUtils.getRequiredCurrentUserPrincipal();
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
        return new UserSummaryDto(user);
    }

    private void checkAccountStatus(User user) {
        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new BadRequestException("This account has been suspended by the administrator.");
        }
        if (user.isAccountLocked()) {
            long remainingMinutes = Duration.between(Instant.now(), user.getLockedUntil()).toMinutes() + 1;
            throw new BadRequestException("Account is temporarily locked due to repeated failed attempts. Please try again in " + remainingMinutes + " minutes.");
        }
    }

    private void handleFailedLoginAttempt(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);
        if (attempts >= 5) {
            user.setStatus(UserStatus.LOCKED);
            user.setLockedUntil(Instant.now().plus(Duration.ofMinutes(lockoutMinutes)));
            logger.warn("User account {} locked for {} minutes due to 5 consecutive failed login attempts", user.getEmail(), lockoutMinutes);
        }
        userRepository.save(user);
    }

    private void establishSession(User user, HttpServletRequest httpRequest) {
        UserPrincipal principal = new UserPrincipal(user);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
    }
}
