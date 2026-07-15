package com.sgsits.portal.auth_service.config;

import com.sgsits.portal.auth_service.model.User;
import com.sgsits.portal.auth_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@lombok.RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Checking database for seeded accounts...");

        // 1. Seed Admin
        if (!userRepository.existsByUsername("admin@sgsits.ac.in")) {
            User admin = new User(
                    null,
                    "admin@sgsits.ac.in",
                    passwordEncoder.encode("admin123"),
                    "ADMIN",
                    "ADMIN",
                    "System Admin",
                    "admin@sgsits.ac.in",
                    true,
                    null,
                    null
            );
            userRepository.save(admin);
            logger.info("Seeded Admin account: admin@sgsits.ac.in");
        }

        logger.info("Database seeding verification complete!");
    }
}
