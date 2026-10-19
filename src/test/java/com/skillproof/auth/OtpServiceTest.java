package com.skillproof.auth;

import com.skillproof.common.BadRequestException;
import com.skillproof.user.Role;
import com.skillproof.user.User;
import com.skillproof.user.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpServiceTest {

    @Mock
    private OtpVerificationRepository otpRepository;

    @Mock
    private EmailService emailService;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private OtpService otpService;

    private User testUser;

    @BeforeEach
    void setUp() {
        otpService = new OtpService(otpRepository, passwordEncoder, emailService);
        ReflectionTestUtils.setField(otpService, "otpExpiryMinutes", 5);
        ReflectionTestUtils.setField(otpService, "otpCooldownSeconds", 60);
        ReflectionTestUtils.setField(otpService, "maxOtpAttempts", 5);

        testUser = new User("dev@example.com", "dev_user", "Dev User", "hashed", Role.STUDENT, UserStatus.ACTIVE);
        testUser.setId(100L);
    }

    @Test
    @DisplayName("Should generate cryptographically secure OTP and dispatch email")
    void shouldGenerateAndSendOtp() {
        when(otpRepository.findTopByUserAndPurposeAndConsumedAtIsNullOrderByCreatedAtDesc(eq(testUser), eq(OtpPurpose.EMAIL_VERIFICATION)))
                .thenReturn(Optional.empty());

        otpService.generateAndSendOtp(testUser, OtpPurpose.EMAIL_VERIFICATION);

        ArgumentCaptor<OtpVerification> captor = ArgumentCaptor.forClass(OtpVerification.class);
        verify(otpRepository).save(captor.capture());

        OtpVerification saved = captor.getValue();
        assertThat(saved.getUser()).isEqualTo(testUser);
        assertThat(saved.getPurpose()).isEqualTo(OtpPurpose.EMAIL_VERIFICATION);
        assertThat(saved.getAttempts()).isEqualTo(0);
        assertThat(saved.isExpired()).isFalse();
        assertThat(saved.isConsumed()).isFalse();

        // Verify email dispatched
        verify(emailService).sendOtpEmail(eq("dev@example.com"), eq("Dev User"), any(String.class), eq(OtpPurpose.EMAIL_VERIFICATION));
    }

    @Test
    @DisplayName("Should enforce cooldown between resend requests")
    void shouldEnforceCooldown() {
        OtpVerification recent = new OtpVerification(testUser, "hash", OtpPurpose.LOGIN, Instant.now().plus(5, ChronoUnit.MINUTES), 5);
        // Created 10 seconds ago (less than 60s cooldown)
        recent.setCreatedAt(Instant.now().minus(10, ChronoUnit.SECONDS));

        when(otpRepository.findTopByUserAndPurposeAndConsumedAtIsNullOrderByCreatedAtDesc(eq(testUser), eq(OtpPurpose.LOGIN)))
                .thenReturn(Optional.of(recent));

        assertThatThrownBy(() -> otpService.generateAndSendOtp(testUser, OtpPurpose.LOGIN))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Please wait");
    }

    @Test
    @DisplayName("Should successfully verify and consume correct OTP")
    void shouldVerifyAndConsumeOtp() {
        String rawOtp = "654321";
        String hashedOtp = passwordEncoder.encode(rawOtp);
        OtpVerification verification = new OtpVerification(testUser, hashedOtp, OtpPurpose.LOGIN, Instant.now().plus(5, ChronoUnit.MINUTES), 5);

        when(otpRepository.findTopByUserAndPurposeAndConsumedAtIsNullOrderByCreatedAtDesc(eq(testUser), eq(OtpPurpose.LOGIN)))
                .thenReturn(Optional.of(verification));

        boolean result = otpService.verifyAndConsumeOtp(testUser, rawOtp, OtpPurpose.LOGIN);

        assertThat(result).isTrue();
        assertThat(verification.isConsumed()).isTrue();
        verify(otpRepository).save(verification);
    }

    @Test
    @DisplayName("Should reject expired OTP and mark consumed")
    void shouldRejectExpiredOtp() {
        String rawOtp = "123456";
        String hashedOtp = passwordEncoder.encode(rawOtp);
        // Expired 1 minute ago
        OtpVerification expired = new OtpVerification(testUser, hashedOtp, OtpPurpose.LOGIN, Instant.now().minus(1, ChronoUnit.MINUTES), 5);

        when(otpRepository.findTopByUserAndPurposeAndConsumedAtIsNullOrderByCreatedAtDesc(eq(testUser), eq(OtpPurpose.LOGIN)))
                .thenReturn(Optional.of(expired));

        assertThatThrownBy(() -> otpService.verifyAndConsumeOtp(testUser, rawOtp, OtpPurpose.LOGIN))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("expired");
    }

    @Test
    @DisplayName("Should track attempts and lock OTP after maximum attempts")
    void shouldLockAfterMaxAttempts() {
        String correctOtp = "999888";
        String wrongOtp = "111222";
        String hashedOtp = passwordEncoder.encode(correctOtp);
        OtpVerification verification = new OtpVerification(testUser, hashedOtp, OtpPurpose.PASSWORD_RESET, Instant.now().plus(5, ChronoUnit.MINUTES), 3);
        verification.setAttempts(2); // 1 attempt remaining

        when(otpRepository.findTopByUserAndPurposeAndConsumedAtIsNullOrderByCreatedAtDesc(eq(testUser), eq(OtpPurpose.PASSWORD_RESET)))
                .thenReturn(Optional.of(verification));

        assertThatThrownBy(() -> otpService.verifyAndConsumeOtp(testUser, wrongOtp, OtpPurpose.PASSWORD_RESET))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Maximum attempts exceeded");

        assertThat(verification.getAttempts()).isEqualTo(3);
        assertThat(verification.isConsumed()).isTrue();
    }
}
