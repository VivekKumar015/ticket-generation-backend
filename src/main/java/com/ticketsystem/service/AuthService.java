package com.ticketsystem.service;

import com.ticketsystem.dto.request.*;
import com.ticketsystem.dto.response.AuthResponse;
import com.ticketsystem.entity.*;
import com.ticketsystem.enums.RoleName;
import com.ticketsystem.exception.ResourceNotFoundException;
import com.ticketsystem.repository.*;
import com.ticketsystem.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest req) {
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

        String token = tokenProvider.generateToken(auth);
        User user = userRepository.findByEmail(req.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String role = user.getRoles().stream()
            .map(r -> r.getName().name())
            .findFirst().orElse("ROLE_USER");

        return AuthResponse.builder()
            .token(token).tokenType("Bearer")
            .userId(user.getId()).email(user.getEmail())
            .firstName(user.getFirstName()).lastName(user.getLastName())
            .role(role).build();
    }

    public String register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        RoleName roleName = (req.getRole() != null)
            ? RoleName.valueOf(req.getRole()) : RoleName.ROLE_USER;

        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        User user = new User();
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setDepartment(req.getDepartment());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setActive(true);
        user.getRoles().add(role);
        userRepository.save(user);
        return "User registered successfully";
    }

    public String changePassword(String email, PasswordChangeRequest req) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
        return "Password changed successfully";
    }
}