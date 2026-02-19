package com.librosmario.pedidos.config;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.librosmario.pedidos.entity.user.Role;
import com.librosmario.pedidos.entity.user.RoleName;
import com.librosmario.pedidos.entity.user.User;
import com.librosmario.pedidos.repository.RoleRepository;
import com.librosmario.pedidos.repository.UserRepository;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initDefaultAdmin(UserRepository userRepository,
                                       RoleRepository roleRepository,
                                       PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                        .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found. Make sure roles are seeded in the database."));

                User admin = new User("Administrador", "admin", "admin@admin.com", passwordEncoder.encode("admin123"));
                admin.setRoles(Collections.singleton(adminRole));
                userRepository.save(admin);

                logger.info("No users found. Default admin user created (username: admin, password: admin123). Please change the password after first login.");
            }
        };
    }
}
