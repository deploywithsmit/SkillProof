package com.skillproof.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillproof.profile.ProfileSettingsRepository;
import com.skillproof.user.User;
import com.skillproof.user.UserRepository;
import com.skillproof.user.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileSettingsRepository profileSettingsRepository;

    @Autowired
    private OtpVerificationRepository otpRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private EmailService emailService;

    @BeforeEach
    void cleanUp() {
        otpRepository.deleteAll();
        profileSettingsRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should validate registration fields and reject malformed requests")
    void shouldRejectInvalidRegistration() throws Exception {
        RegisterRequest badRequest = new RegisterRequest("", "invalid-email", "a", "short");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.fieldErrors", hasSize(greaterThanOrEqualTo(3))));
    }

    @Test
    @DisplayName("Should successfully register student in pending verification state")
    void shouldRegisterStudentPendingVerification() throws Exception {
        RegisterRequest request = new RegisterRequest("Jane Doe", "jane@example.com", "jane_doe", "Password@123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.redirectUrl").value(containsString("verify-email")));

        User user = userRepository.findByEmailIgnoreCase("jane@example.com").orElse(null);
        assertThat(user).isNotNull();
        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING_VERIFICATION);
        assertThat(passwordEncoder.matches("Password@123", user.getPasswordHash())).isTrue();
    }

    @Test
    @DisplayName("Should reject unauthenticated access to protected dashboard API")
    void shouldRejectUnauthenticatedAccess() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/overview")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    @DisplayName("Should verify email OTP, activate user, and establish authenticated session")
    void shouldVerifyEmailAndAuthenticate() throws Exception {
        // Prepare pending user
        User user = new User("active@example.com", "active_u", "Active User", passwordEncoder.encode("Password@123"),
                com.skillproof.user.Role.STUDENT, UserStatus.PENDING_VERIFICATION);
        user = userRepository.save(user);

        // Seed valid OTP
        String rawOtp = "445566";
        OtpVerification verification = new OtpVerification(
                user,
                passwordEncoder.encode(rawOtp),
                OtpPurpose.EMAIL_VERIFICATION,
                Instant.now().plus(5, ChronoUnit.MINUTES),
                5
        );
        otpRepository.save(verification);

        VerifyOtpRequest verifyRequest = new VerifyOtpRequest("active@example.com", rawOtp, OtpPurpose.EMAIL_VERIFICATION);

        MockHttpSession session = (MockHttpSession) mockMvc.perform(post("/api/v1/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.user.email").value("active@example.com"))
                .andExpect(jsonPath("$.data.user.status").value("ACTIVE"))
                .andReturn()
                .getRequest()
                .getSession();

        // User should now be ACTIVE in database
        User updated = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(UserStatus.ACTIVE);

        // Session should be authenticated and able to access protected dashboard API
        mockMvc.perform(get("/api/v1/dashboard/overview")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.welcomeName").value("Active User"));
    }
}
