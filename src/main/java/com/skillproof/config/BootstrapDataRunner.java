package com.skillproof.config;

import com.skillproof.user.Role;
import com.skillproof.user.User;
import com.skillproof.user.UserRepository;
import com.skillproof.user.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BootstrapDataRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(BootstrapDataRunner.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public BootstrapDataRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Ensure default accounts have valid BCrypt passwords
        updatePasswordIfPresent("admin@skillproof.io", "Admin@12345");
        updatePasswordIfPresent("sarah.chen@skillproof.io", "Password@123");
        updatePasswordIfPresent("marcus.vance@skillproof.io", "Password@123");
        updatePasswordIfPresent("devon.student@skillproof.io", "Password@123");
        updatePasswordIfPresent("priya.patel@skillproof.io", "Password@123");

        logger.info("SkillProof platform bootstrapped successfully. Ready for practical skill verification.");
    }

    private void updatePasswordIfPresent(String email, String rawPassword) {
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Encode clean BCrypt hash
            user.setPasswordHash(passwordEncoder.encode(rawPassword));
            user.setStatus(UserStatus.ACTIVE);
            userRepository.save(user);
        }
    }
}
