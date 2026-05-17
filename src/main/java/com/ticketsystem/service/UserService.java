package com.ticketsystem.service;

import com.ticketsystem.dto.response.UserResponse;
import com.ticketsystem.entity.User;
import com.ticketsystem.enums.RoleName;
import com.ticketsystem.exception.ResourceNotFoundException;
import com.ticketsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> getAllEmployees() {
        return userRepository.findByRoleName(RoleName.ROLE_SUPPORT_EMPLOYEE)
            .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
            .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToResponse(user);
    }

    private UserResponse mapToResponse(User user) {
        String role = user.getRoles().stream()
            .map(r -> r.getName().name())
            .findFirst().orElse("ROLE_USER");
        return UserResponse.builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .phone(user.getPhone())
            .department(user.getDepartment())
            .role(role)
            .active(user.getActive())
            .createdAt(user.getCreatedAt())
            .build();
    }
}   