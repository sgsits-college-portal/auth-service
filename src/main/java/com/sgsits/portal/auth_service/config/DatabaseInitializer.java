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

        // 2. Seed Student (Ayush Solanki)
        if (!userRepository.existsByUsername("0801CS241037")) {
            User student = new User(
                    null,
                    "0801CS241037",
                    passwordEncoder.encode("ayush123"),
                    "STUDENT",
                    "STUDENT",
                    "Ayush Solanki",
                    "ayush.solanki@example.com",
                    true,
                    null,
                    null
            );
            userRepository.save(student);
            logger.info("Seeded Student account: 0801CS241037 (Ayush Solanki)");
        }

        // 3. Seed Librarian (Staff)
        if (!userRepository.existsByUsername("librarian1@sgsits.ac.in")) {
            User librarian = new User(
                    null,
                    "librarian1@sgsits.ac.in",
                    passwordEncoder.encode("librarian1"),
                    "STAFF",
                    "LIBRARIAN",
                    "Librarian Staff",
                    "librarian1@sgsits.ac.in",
                    true,
                    null,
                    null
            );
            userRepository.save(librarian);
            logger.info("Seeded Librarian account: librarian1@sgsits.ac.in");
        }

        // 4. Seed Faculty
        if (!userRepository.existsByUsername("faculty1@sgsits.ac.in")) {
            User faculty = new User(
                    null,
                    "faculty1@sgsits.ac.in",
                    passwordEncoder.encode("faculty123"),
                    "FACULTY",
                    "FACULTY",
                    "Dr. Rajesh Kumar",
                    "faculty1@sgsits.ac.in",
                    true,
                    null,
                    null
            );
            userRepository.save(faculty);
            logger.info("Seeded Faculty account: faculty1@sgsits.ac.in");
        }

        // 5. Seed Department Head (HOD)
        if (!userRepository.existsByUsername("hod_cs@sgsits.ac.in")) {
            User hod = new User(
                    null,
                    "hod_cs@sgsits.ac.in",
                    passwordEncoder.encode("hod123"),
                    "HEAD",
                    "HEAD",
                    "Dr. Sunita Verma",
                    "hod_cs@sgsits.ac.in",
                    true,
                    null,
                    null
            );
            userRepository.save(hod);
            logger.info("Seeded HOD account: hod_cs@sgsits.ac.in");
        }

        logger.info("Database seeding verification complete!");
    }
}
