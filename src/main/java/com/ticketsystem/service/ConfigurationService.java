package com.ticketsystem.service;

import com.ticketsystem.dto.request.*;
import com.ticketsystem.dto.response.*;
import com.ticketsystem.entity.*;
import com.ticketsystem.enums.RoleName;
import com.ticketsystem.exception.ResourceNotFoundException;
import com.ticketsystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


import com.ticketsystem.dto.request.EmployeeRequest;
import com.ticketsystem.entity.Role;

import com.ticketsystem.repository.RoleRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.LinkedHashMap;

@Service
@RequiredArgsConstructor
public class ConfigurationService {

    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ShiftRepository shiftRepository;
    private final EmployeeProjectRepository empProjectRepository;
    private final TicketRepository ticketRepository;

    // ===== PROJECT MANAGEMENT =====

    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll().stream()
            .map(this::mapProjectToResponse)
            .collect(Collectors.toList());
    }

    public List<ProjectResponse> getActiveProjects() {
        return projectRepository.findByActiveTrue().stream()
            .map(this::mapProjectToResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public ProjectResponse createProject(ProjectRequest req) {
        Project project = new Project();
        project.setName(req.getName());
        project.setProjectCode(req.getProjectCode());
        project.setDescription(req.getDescription());
        project.setSupportEmail(req.getSupportEmail());
        project.setSlaHours(req.getSlaHours() != null ? req.getSlaHours() : 24);
        project.setActive(true);

        if (req.getShiftId() != null) {
            Shift shift = shiftRepository.findById(req.getShiftId()).orElse(null);
            project.setShift(shift);
        }

        return mapProjectToResponse(projectRepository.save(project));
    }

    @Transactional
    public ProjectResponse updateProject(Long id, ProjectRequest req) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (req.getName() != null) project.setName(req.getName());
        if (req.getProjectCode() != null) project.setProjectCode(req.getProjectCode());
        if (req.getDescription() != null) project.setDescription(req.getDescription());
        if (req.getSupportEmail() != null) project.setSupportEmail(req.getSupportEmail());
        if (req.getSlaHours() != null) project.setSlaHours(req.getSlaHours());
        if (req.getActive() != null) project.setActive(req.getActive());

        if (req.getShiftId() != null) {
            Shift shift = shiftRepository.findById(req.getShiftId()).orElse(null);
            project.setShift(shift);
        }

        return mapProjectToResponse(projectRepository.save(project));
    }

    @Transactional
    public void deleteProject(Long id) {
        projectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        projectRepository.deleteById(id);
    }

    // ===== SHIFT MANAGEMENT =====

    public List<Shift> getAllShifts() {
        return shiftRepository.findAll();
    }

    @Transactional
    public Shift createShift(ShiftRequest req) {
        Shift shift = new Shift();
        shift.setName(req.getName());
        shift.setStartTime(LocalTime.parse(req.getStartTime()));
        shift.setEndTime(LocalTime.parse(req.getEndTime()));
        shift.setTimezone(req.getTimezone() != null ? req.getTimezone() : "Asia/Kolkata");
        shift.setActive(true);
        return shiftRepository.save(shift);
    }

    @Transactional
    public Shift updateShift(Long id, ShiftRequest req) {
        Shift shift = shiftRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Shift not found"));
        if (req.getName() != null) shift.setName(req.getName());
        if (req.getStartTime() != null) shift.setStartTime(LocalTime.parse(req.getStartTime()));
        if (req.getEndTime() != null) shift.setEndTime(LocalTime.parse(req.getEndTime()));
        if (req.getTimezone() != null) shift.setTimezone(req.getTimezone());
        return shiftRepository.save(shift);
    }

    // ===== EMPLOYEE PROJECT MAPPING =====

    @Transactional
    public String assignEmployeeToProjects(EmployeeProjectRequest req) {
        User user = userRepository.findById(req.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        for (Long projectId : req.getProjectIds()) {
            Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

            // Check if mapping already exists
            Optional<EmployeeProjectMapping> existing =
                empProjectRepository.findByUserAndProject(user, project);

            if (existing.isPresent()) {
                EmployeeProjectMapping mapping = existing.get();
                mapping.setActive(true);
                if (req.getRoleInProject() != null)
                    mapping.setRoleInProject(req.getRoleInProject());
                empProjectRepository.save(mapping);
            } else {
                EmployeeProjectMapping mapping = new EmployeeProjectMapping();
                mapping.setUser(user);
                mapping.setProject(project);
                mapping.setRoleInProject(req.getRoleInProject());
                mapping.setActive(true);
                empProjectRepository.save(mapping);
            }
        }
        return "Employee assigned to projects successfully";
    }

    @Transactional
    public String removeEmployeeFromProject(Long userId, Long projectId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        empProjectRepository.findByUserAndProject(user, project).ifPresent(m -> {
            m.setActive(false);
            empProjectRepository.save(m);
        });

        return "Employee removed from project";
    }

    public List<Map<String, Object>> getProjectEmployees(Long projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        return empProjectRepository.findByProjectAndActiveTrue(project).stream()
            .map(m -> {
                Map<String, Object> map = new HashMap<>();
                map.put("userId", m.getUser().getId());
                map.put("name", m.getUser().getFirstName() + " " + m.getUser().getLastName());
                map.put("email", m.getUser().getEmail());
                map.put("roleInProject", m.getRoleInProject());
                map.put("assignedAt", m.getAssignedAt());
                return map;
            }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getEmployeeProjects(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return empProjectRepository.findByUserAndActiveTrue(user).stream()
            .map(m -> {
                Map<String, Object> map = new HashMap<>();
                map.put("projectId", m.getProject().getId());
                map.put("projectName", m.getProject().getName());
                map.put("projectCode", m.getProject().getProjectCode());
                map.put("roleInProject", m.getRoleInProject());
                return map;
            }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getAllEmployees() {
        return userRepository.findByRoleName(RoleName.ROLE_SUPPORT_EMPLOYEE).stream()
            .map(u -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", u.getId());
                map.put("name", u.getFirstName() + " " + u.getLastName());
                map.put("email", u.getEmail());
                map.put("department", u.getDepartment());
                map.put("active", u.getActive());

                // Get assigned projects
                List<String> projects = empProjectRepository
                    .findByUserAndActiveTrue(u).stream()
                    .map(m -> m.getProject().getName())
                    .collect(Collectors.toList());
                map.put("assignedProjects", projects);

                return map;
            }).collect(Collectors.toList());
    }

    // ===== HELPER =====

    private ProjectResponse mapProjectToResponse(Project p) {
        Long total = ticketRepository.countByProject(p);
        Long open = ticketRepository.countByProjectAndStatus(p, com.ticketsystem.enums.TicketStatus.OPEN);
        Long resolved = ticketRepository.countByProjectAndStatus(p, com.ticketsystem.enums.TicketStatus.RESOLVED);

        String shiftName = null, shiftStart = null, shiftEnd = null;
        if (p.getShift() != null) {
            shiftName = p.getShift().getName();
            shiftStart = p.getShift().getStartTime().toString();
            shiftEnd = p.getShift().getEndTime().toString();
        }

        return ProjectResponse.builder()
            .id(p.getId()).name(p.getName())
            .projectCode(p.getProjectCode())
            .description(p.getDescription())
            .supportEmail(p.getSupportEmail())
            .slaHours(p.getSlaHours())
            .shiftName(shiftName).shiftStartTime(shiftStart).shiftEndTime(shiftEnd)
            .active(p.getActive())
            .totalTickets(total).openTickets(open).resolvedTickets(resolved)
            .createdAt(p.getCreatedAt())
            .build();
    }

    // ===== EMPLOYEE CRUD =====

public List<Map<String, Object>> getAllUsersWithRoles() {
    return userRepository.findAll().stream()
        .map(u -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", u.getId());
            map.put("firstName", u.getFirstName());
            map.put("lastName", u.getLastName());
            map.put("email", u.getEmail());
            map.put("phone", u.getPhone());
            map.put("department", u.getDepartment());
            map.put("active", u.getActive());
            String role = u.getRoles().stream()
                .map(r -> r.getName().name())
                .findFirst().orElse("ROLE_USER");
            map.put("role", role);
            List<String> projects = empProjectRepository
                .findByUserAndActiveTrue(u).stream()
                .map(m -> m.getProject().getName())
                .collect(Collectors.toList());
            map.put("assignedProjects", projects);
            map.put("createdAt", u.getCreatedAt());
            return map;
        }).collect(Collectors.toList());
}

@Transactional
public Map<String, Object> createEmployee(EmployeeRequest req) {
    if (userRepository.existsByEmail(req.getEmail())) {
        throw new RuntimeException("Email already registered");
    }

    RoleName roleName = (req.getRole() != null)
        ? RoleName.valueOf(req.getRole()) : RoleName.ROLE_SUPPORT_EMPLOYEE;

    Role role = roleRepository.findByName(roleName)
        .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

    User user = new User();
    user.setFirstName(req.getFirstName());
    user.setLastName(req.getLastName());
    user.setEmail(req.getEmail());
    user.setPhone(req.getPhone());
    user.setDepartment(req.getDepartment());
    user.setPassword(passwordEncoder.encode(
        req.getPassword() != null ? req.getPassword() : "Welcome@123"));
    user.setActive(true);
    user.getRoles().add(role);
    User saved = userRepository.save(user);

    Map<String, Object> map = new LinkedHashMap<>();
    map.put("id", saved.getId());
    map.put("firstName", saved.getFirstName());
    map.put("lastName", saved.getLastName());
    map.put("email", saved.getEmail());
    map.put("role", roleName.name());
    map.put("active", saved.getActive());
    return map;
}

@Transactional
public Map<String, Object> updateEmployee(Long id, EmployeeRequest req) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    if (req.getFirstName() != null) user.setFirstName(req.getFirstName());
    if (req.getLastName() != null) user.setLastName(req.getLastName());
    if (req.getPhone() != null) user.setPhone(req.getPhone());
    if (req.getDepartment() != null) user.setDepartment(req.getDepartment());
    if (req.getActive() != null) user.setActive(req.getActive());
    if (req.getPassword() != null && !req.getPassword().isEmpty()) {
        user.setPassword(passwordEncoder.encode(req.getPassword()));
    }

    if (req.getRole() != null) {
        RoleName newRoleName = RoleName.valueOf(req.getRole());
        Role newRole = roleRepository.findByName(newRoleName)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        user.getRoles().clear();
        user.getRoles().add(newRole);
    }

    User saved = userRepository.save(user);
    String role = saved.getRoles().stream()
        .map(r -> r.getName().name())
        .findFirst().orElse("ROLE_USER");

    Map<String, Object> map = new LinkedHashMap<>();
    map.put("id", saved.getId());
    map.put("firstName", saved.getFirstName());
    map.put("lastName", saved.getLastName());
    map.put("email", saved.getEmail());
    map.put("phone", saved.getPhone());
    map.put("department", saved.getDepartment());
    map.put("role", role);
    map.put("active", saved.getActive());
    return map;
}

@Transactional
public String deleteEmployee(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    // Remove from all project mappings first
    List<EmployeeProjectMapping> mappings =
        empProjectRepository.findByUserAndActiveTrue(user);
    mappings.forEach(m -> {
        m.setActive(false);
        empProjectRepository.save(m);
    });

    // Deactivate instead of hard delete for data integrity
    user.setActive(false);
    userRepository.save(user);
    return "Employee deactivated successfully";
}

@Transactional
public String toggleEmployeeStatus(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    user.setActive(!user.getActive());
    userRepository.save(user);
    return user.getActive() ? "Employee activated" : "Employee deactivated";
}
}