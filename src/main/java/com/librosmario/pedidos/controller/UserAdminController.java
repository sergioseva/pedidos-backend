package com.librosmario.pedidos.controller;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.librosmario.pedidos.entity.user.Role;
import com.librosmario.pedidos.entity.user.RoleName;
import com.librosmario.pedidos.entity.user.User;
import com.librosmario.pedidos.exception.AppException;
import com.librosmario.pedidos.payload.ApiResponse;
import com.librosmario.pedidos.payload.UserAdminResponse;
import com.librosmario.pedidos.repository.RoleRepository;
import com.librosmario.pedidos.repository.UserRepository;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserAdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<UserAdminResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toResponse(userOpt.get()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserAdminResponse request) {
        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();

        // Validate username uniqueness (excluding current user)
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                return new ResponseEntity<>(new ApiResponse(false, "Username is already taken!"),
                        HttpStatus.BAD_REQUEST);
            }
        }

        // Validate email uniqueness (excluding current user)
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                return new ResponseEntity<>(new ApiResponse(false, "Email Address already in use!"),
                        HttpStatus.BAD_REQUEST);
            }
        }

        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        // Update password if provided
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Update role if provided
        if (request.getRole() != null) {
            RoleName roleName = RoleName.valueOf(request.getRole());
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new AppException("User Role not set."));
            HashSet<Role> roles = new HashSet<>();
            roles.add(role);
            user.setRoles(roles);
        }

        userRepository.save(user);
        return ResponseEntity.ok(toResponse(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok(new ApiResponse(true, "User deleted successfully"));
    }

    private UserAdminResponse toResponse(User user) {
        String roleName = user.getRoles().stream()
                .findFirst()
                .map(r -> r.getName().name())
                .orElse("ROLE_USER");
        return new UserAdminResponse(user.getId(), user.getName(), user.getUsername(), user.getEmail(), roleName);
    }
}
