package com.sgsits.portal.auth_service.controller;

import com.sgsits.portal.auth_service.constants.RoleConstants;
import com.sgsits.portal.auth_service.constants.RoleValidator;
import com.sgsits.portal.auth_service.model.User;
import com.sgsits.portal.auth_service.payload.*;
import com.sgsits.portal.auth_service.repository.UserRepository;
import com.sgsits.portal.auth_service.security.JwtUtils;
import com.sgsits.portal.auth_service.security.services.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

            // Validate role mismatch
            if (loginRequest.getRole() != null && !userPrincipal.getRole().equalsIgnoreCase(loginRequest.getRole())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Role mismatch."));
            }

            String jwt = jwtUtils.generateJwtToken(authentication);

            return ResponseEntity.ok(new JwtResponse(jwt,
                    userPrincipal.getId(),
                    userPrincipal.getUsername(),
                    userPrincipal.getRole(),
                    userPrincipal.getSubRole(),
                    userPrincipal.getFullName(),
                    userPrincipal.getEmail()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid username or password."));
        }
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest signUpRequest) {
        // 1. Role Validation using our new Utility
        if (!RoleValidator.isValidRole(signUpRequest.getRole())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid role provided!"));
        }
        if (!RoleValidator.isValidSubRole(signUpRequest.getSubRole())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid sub-role provided!"));
        }

        // 2. Duplicate checks
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username taken!"));
        }

        // 3. Normalization
        String roleStr = signUpRequest.getRole().toUpperCase();
        String subRoleStr = signUpRequest.getSubRole();
        if (subRoleStr == null || subRoleStr.trim().isEmpty() || "NONE".equalsIgnoreCase(subRoleStr.trim())) {
            subRoleStr = roleStr;
        } else {
            subRoleStr = subRoleStr.toUpperCase().trim();
        }

        User user = new User(
                null,
                signUpRequest.getUsername(),
                encoder.encode(signUpRequest.getPassword()),
                roleStr,
                subRoleStr,
                signUpRequest.getFullName(),
                signUpRequest.getEmail(),
                true,
                null,
                null
        );

        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("No active session."));
        }
        return ResponseEntity.ok((UserDetailsImpl) authentication.getPrincipal());
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody RegisterRequest updateRequest) {
        if (updateRequest.getRole() != null && !RoleValidator.isValidRole(updateRequest.getRole())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid role provided!"));
        }
        if (updateRequest.getSubRole() != null && !RoleValidator.isValidSubRole(updateRequest.getSubRole())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid sub-role provided!"));
        }

        return userRepository.findById(id).map(user -> {
            user.setFullName(updateRequest.getFullName());
            user.setEmail(updateRequest.getEmail());
            String roleStr = updateRequest.getRole().toUpperCase();
            user.setRole(roleStr);

            String subRoleStr = updateRequest.getSubRole();
            if (subRoleStr == null || subRoleStr.trim().isEmpty() || "NONE".equalsIgnoreCase(subRoleStr.trim())) {
                subRoleStr = roleStr;
            } else {
                subRoleStr = subRoleStr.toUpperCase().trim();
            }
            user.setSubRole(subRoleStr);
            if (updateRequest.getPassword() != null && !updateRequest.getPassword().trim().isEmpty()) {
                user.setPassword(encoder.encode(updateRequest.getPassword()));
            }
            userRepository.save(user);
            return ResponseEntity.ok(new MessageResponse("User updated successfully!"));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.ok(new MessageResponse("User deleted successfully!"));
        }
        return ResponseEntity.notFound().build();
    }
}
