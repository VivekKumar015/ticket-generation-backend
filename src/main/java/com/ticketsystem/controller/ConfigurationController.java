package com.ticketsystem.controller;

import com.ticketsystem.dto.request.*;
import com.ticketsystem.dto.response.ProjectResponse;
import com.ticketsystem.entity.Shift;
import com.ticketsystem.service.ConfigurationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class ConfigurationController {

    private final ConfigurationService configService;

    // ===== PROJECTS =====

    @GetMapping("/projects")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return ResponseEntity.ok(configService.getAllProjects());
    }

    @PostMapping("/projects")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody ProjectRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(configService.createProject(req));
    }

    @PutMapping("/projects/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long id,
            @RequestBody ProjectRequest req) {
        return ResponseEntity.ok(configService.updateProject(id, req));
    }

    @DeleteMapping("/projects/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> deleteProject(@PathVariable Long id) {
        configService.deleteProject(id);
        return ResponseEntity.ok("Project deleted");
    }

    // ===== SHIFTS =====

    @GetMapping("/shifts")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<Shift>> getAllShifts() {
        return ResponseEntity.ok(configService.getAllShifts());
    }

    @PostMapping("/shifts")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Shift> createShift(
            @Valid @RequestBody ShiftRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(configService.createShift(req));
    }

    @PutMapping("/shifts/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Shift> updateShift(
            @PathVariable Long id,
            @RequestBody ShiftRequest req) {
        return ResponseEntity.ok(configService.updateShift(id, req));
    }

    // ===== EMPLOYEE PROJECT MAPPING =====

    @PostMapping("/employee-project")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> assignEmployeeToProjects(
            @RequestBody EmployeeProjectRequest req) {
        return ResponseEntity.ok(configService.assignEmployeeToProjects(req));
    }

    @DeleteMapping("/employee-project/{userId}/project/{projectId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> removeEmployeeFromProject(
            @PathVariable Long userId,
            @PathVariable Long projectId) {
        return ResponseEntity.ok(
            configService.removeEmployeeFromProject(userId, projectId));
    }

    @GetMapping("/projects/{projectId}/employees")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getProjectEmployees(
            @PathVariable Long projectId) {
        return ResponseEntity.ok(configService.getProjectEmployees(projectId));
    }

    @GetMapping("/employees/{userId}/projects")
    public ResponseEntity<List<Map<String, Object>>> getEmployeeProjects(
            @PathVariable Long userId) {
        return ResponseEntity.ok(configService.getEmployeeProjects(userId));
    }

    @GetMapping("/employees")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getAllEmployees() {
        return ResponseEntity.ok(configService.getAllEmployees());
    }
}