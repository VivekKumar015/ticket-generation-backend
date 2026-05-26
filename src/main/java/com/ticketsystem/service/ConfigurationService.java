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

@Service
@RequiredArgsConstructor
public class ConfigurationService {

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
        Project project = Project.builder()
            .name(req.getName())
            .projectCode(req.getProjectCode())
            .description(req.getDescription())
            .supportEmail(req.getSupportEmail())
            .slaHours(req.getSlaHours() != null ? req.getSlaHours() : 24)
            .active(true)
            .build();

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
        Shift shift = Shift.builder()
            .name(req.getName())
            .startTime(LocalTime.parse(req.getStartTime()))
            .endTime(LocalTime.parse(req.getEndTime()))
            .timezone(req.getTimezone() != null ? req.getTimezone() : "Asia/Kolkata")
            .active(true)
            .build();
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
                EmployeeProjectMapping mapping = EmployeeProjectMapping.builder()
                    .user(user)
                    .project(project)
                    .roleInProject(req.getRoleInProject())
                    .active(true)
                    .build();
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
}