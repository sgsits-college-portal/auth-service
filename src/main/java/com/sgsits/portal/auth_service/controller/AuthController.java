package com.sgsits.portal.auth_service.controller;

import com.sgsits.portal.auth_service.model.User;
import com.sgsits.portal.auth_service.payload.*;
import com.sgsits.portal.auth_service.repository.UserRepository;
import com.sgsits.portal.auth_service.security.JwtUtils;
import com.sgsits.portal.auth_service.security.services.UserDetailsImpl;
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
@lombok.RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            // 1. Authenticate username and password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

            // 2. Validate Role Mismatch
            // Compare requesting role with actual role in DB
            if (loginRequest.getRole() != null && !userPrincipal.getRole().equalsIgnoreCase(loginRequest.getRole())) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Role mismatch. You cannot log in as " + loginRequest.getRole() + " using these credentials."));
            }

            // 3. Generate Token
            String jwt = jwtUtils.generateJwtToken(authentication);

            return ResponseEntity.ok(new JwtResponse(jwt,
                    userPrincipal.getId(),
                    userPrincipal.getUsername(),
                    userPrincipal.getRole(),
                    userPrincipal.getSubRole(),
                    userPrincipal.getFullName(),
                    userPrincipal.getEmail()));
        } catch (BadCredentialsException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Invalid username or password."));
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(new MessageResponse("Error occurred during authentication: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest signUpRequest) {
        // 1. Check if username exists
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        // 2. Check if email exists
        if (signUpRequest.getEmail() != null && !signUpRequest.getEmail().trim().isEmpty() 
                && userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // 3. Normalize values
        String roleStr = signUpRequest.getRole() != null ? signUpRequest.getRole().toUpperCase() : "STUDENT";
        String subRoleStr = signUpRequest.getSubRole() != null ? signUpRequest.getSubRole().toUpperCase() : null;

        // Create new user's account
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
            return ResponseEntity.badRequest().body(new MessageResponse("No active session found."));
        }
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(userPrincipal);
    }
}
