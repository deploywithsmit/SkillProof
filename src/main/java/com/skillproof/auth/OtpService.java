package com.skillproof.auth;

import com.skillproof.common.BadRequestException;
import com.skillproof.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class OtpService {

    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);
    private static final SecureRandom secureRandom = new SecureRandom();

    private final OtpVerificationRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.auth.otp-expiry-minutes:5}")
    private int otpExpiryMinutes;

    @Value("${app.auth.otp-cooldown-seconds:60}")
    private int otpCooldownSeconds;

    @Value("${app.auth.max-otp-attempts:5}")
    private int maxOtpAttempts;

    public OtpService(OtpVerificationRepository otpRepository,
                      PasswordEncoder passwordEncoder,
                      EmailService emailService) {
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public void generateAndSendOtp(User user, OtpPurpose purpose) {
        // Check for cooldown from the most recent active OTP
        Optional<OtpVerification> existingOpt = otpRepository.findTopByUserAndPurposeAndConsumedAtIsNullOrderByCreatedAtDesc(user, purpose);
        if (existingOpt.isPresent()) {
            OtpVerification existing = existingOpt.get();
            long secondsSinceLastOtp = Duration.between(existing.getCreatedAt(), Instant.now()).getSeconds();
            if (secondsSinceLastOtp < otpCooldownSeconds) {
                long waitTime = otpCooldownSeconds - secondsSinceLastOtp;
                throw new BadRequestException("Please wait " + waitTime + " seconds before requesting another code.");
            }
            // Invalidate/supersede prior OTPs for this purpose
            otpRepository.invalidateActiveOtps(user, purpose, Instant.now());
        }

        // Generate cryptographically secure 6-digit numeric string
        int code = 100000 + secureRandom.nextInt(900000);
        String rawOtp = String.valueOf(code);

        // Store secure hash
        String hashedOtp = passwordEncoder.encode(rawOtp);
        Instant expiresAt = Instant.now().plus(Duration.ofMinutes(otpExpiryMinutes));

        OtpVerification verification = new OtpVerification(user, hashedOtp, purpose, expiresAt, maxOtpAttempts);
        otpRepository.save(verification);

        // Send via Email Service
        emailService.sendOtpEmail(user.getEmail(), user.getFullName(), rawOtp, purpose);
    }

    @Transactional
    public boolean verifyAndConsumeOtp(User user, String rawOtp, OtpPurpose purpose) {
        Optional<OtpVerification> opt = otpRepository.findTopByUserAndPurposeAndConsumedAtIsNullOrderByCreatedAtDesc(user, purpose);

        if (opt.isEmpty()) {
            throw new BadRequestException("No active verification code found. Please request a new code.");
        }

        OtpVerification verification = opt.get();

        if (verification.isConsumed()) {
            throw new BadRequestException("This verification code has already been used. Please request a new code.");
        }

        if (verification.isExpired()) {
            verification.setConsumedAt(Instant.now());
            otpRepository.save(verification);
            throw new BadRequestException("Verification code has expired. Please request a new code.");
        }

        if (verification.isExhausted()) {
            verification.setConsumedAt(Instant.now());
            otpRepository.save(verification);
            throw new BadRequestException("Too many failed attempts. This code is now invalid. Please request a new code.");
        }

        // Increment attempt count
        verification.setAttempts(verification.getAttempts() + 1);

        // Constant-time check via PasswordEncoder (BCrypt)
        boolean matches = passwordEncoder.matches(rawOtp, verification.getOtpHash());

        if (!matches) {
            int remaining = verification.getMaxAttempts() - verification.getAttempts();
            if (remaining <= 0) {
                verification.setConsumedAt(Instant.now());
                otpRepository.save(verification);
                throw new BadRequestException("Invalid code. Maximum attempts exceeded. Please request a new code.");
            } else {
                otpRepository.save(verification);
                throw new BadRequestException("Invalid verification code. " + remaining + " attempts remaining.");
            }
        }

        // Successfully verified: consume OTP
        verification.setConsumedAt(Instant.now());
        otpRepository.save(verification);
        logger.info("Successfully verified OTP for user {} [purpose: {}]", user.getEmail(), purpose);
        return true;
    }
}
