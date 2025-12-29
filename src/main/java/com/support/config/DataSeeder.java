package com.support.config;

import com.support.entity.User;
import com.support.entity.UserRole;
import com.support.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    @Bean
    public CommandLineRunner seedUsers() {
        return args -> {

            createUserIfMissing(
                    "admin@gmail.com",
                    "Admin",
                    "password",
                    UserRole.ADMIN
            );

            createUserIfMissing(
                    "employee@gmail.com",
                    "Employee",
                    "password",
                    UserRole.EMPLOYEE
            );

            createUserIfMissing(
                    "resolver@gmail.com",
                    "Resolver",
                    "password",
                    UserRole.RESOLVER
            );

            System.out.println("➡ Default users verified / created");
        };
    }

    private void createUserIfMissing(String email, String name, String rawPassword, UserRole role) {

            userRepo.findByEmail(email).ifPresentOrElse(
                u -> System.out.println("✔ User already exists: " + email),

                () -> {
                    User u = new User();
                    u.setEmail(email);
                    u.setName(name);
                    u.setPassword(encoder.encode(rawPassword));
                    u.setRole(role);

                    userRepo.save(u);

                    System.out.println("✅ Created default user: " + email);
                }
            );
    }
}
