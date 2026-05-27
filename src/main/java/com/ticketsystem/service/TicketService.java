package com.ticketsystem.service;

import com.ticketsystem.dto.request.TicketRequest;
import com.ticketsystem.dto.response.TicketResponse;
import com.ticketsystem.entity.*;
import com.ticketsystem.enums.*;
import com.ticketsystem.exception.ResourceNotFoundException;
import com.ticketsystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final NotificationService notificationService;
    private final ActivityLogRepository activityLogRepository;
    private final EmployeeProjectRepository empProjectRepository;
    private final SlaService slaService;

    @Transactional
    public TicketResponse createTicket(TicketRequest req, String userEmail) {
        User creator = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Ticket ticket = new Ticket();
        ticket.setTicketNumber(generateTicketNumber());
        ticket.setTitle(req.getTitle());
        ticket.setDescription(req.getDescription());
        ticket.setCategory(req.getCategory());
        ticket.setPriority(req.getPriority() != null ? req.getPriority() : Priority.P3_MEDIUM);
        ticket.setSupportLevel(req.getSupportLevel() != null ? req.getSupportLevel() : SupportLevel.L1);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setSlaStatus(SlaStatus.WITHIN_SLA);
        ticket.setCreatedBy(creator);
        ticket.setComments(new ArrayList<>());
        ticket.setAttachments(new ArrayList<>());

        if (req.getProjectId() != null) {
            Project project = projectRepository.findById(req.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
            ticket.setProject(project);

            try {
                int slaHours = project.getSlaHours() != null ? project.getSlaHours() : 24;
                LocalDateTime slaBreachTime = slaService.calculateSlaBreachTime(
                    LocalDateTime.now(), slaHours, project.getShift());
                ticket.setSlaBreachTime(slaBreachTime);
            } catch (Exception e) {
                System.err.println("SLA calculation failed: " + e.getMessage());
            }
        }

        if (req.getAssignedToId() != null) {
            User assignee = userRepository.findById(req.getAssignedToId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
            ticket.setAssignedTo(assignee);
        }

        Ticket saved = ticketRepository.save(ticket);
        logActivity(saved, creator, "CREATED", null, "OPEN", "status");

        if (saved.getAssignedTo() != null) {
            notificationService.sendNotification(
                saved.getAssignedTo(),
                "New ticket assigned: " + saved.getTicketNumber(),
                "ASSIGNED", saved);
        }

        return mapToResponse(saved);
    }

    @Transactional
    public TicketResponse updateTicket(Long id, TicketRequest req, String userEmail) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        User updater = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (req.getStatus() != null && !req.getStatus().equals(ticket.getStatus())) {
            logActivity(ticket, updater, "STATUS_CHANGED",
                ticket.getStatus() != null ? ticket.getStatus().name() : null,
                req.getStatus().name(), "status");

            if (req.getStatus() == TicketStatus.RESOLVED) {
                ticket.setResolvedAt(LocalDateTime.now());
                try {
                    if (ticket.getCreatedAt() != null && ticket.getProject() != null) {
                        double workingHours = slaService.calculateWorkingHours(
                            ticket.getCreatedAt(),
                            LocalDateTime.now(),
                            ticket.getProject().getShift());
                        ticket.setWorkingHoursResolution(workingHours);
                    }
                } catch (Exception e) {
                    System.err.println("Working hours calculation failed: " + e.getMessage());
                }
            }
            ticket.setStatus(req.getStatus());

            try {
                notificationService.sendNotification(
                    ticket.getCreatedBy(),
                    "Ticket " + ticket.getTicketNumber() + " status updated to: " + req.getStatus(),
                    "STATUS_UPDATE", ticket);
            } catch (Exception e) {
                System.err.println("Notification failed: " + e.getMessage());
            }
        }

        if (req.getAssignedToId() != null) {
            User assignee = userRepository.findById(req.getAssignedToId()).orElse(null);
            ticket.setAssignedTo(assignee);
            if (assignee != null) {
                try {
                    notificationService.sendNotification(assignee,
                        "Ticket " + ticket.getTicketNumber() + " assigned to you",
                        "ASSIGNED", ticket);
                } catch (Exception e) {
                    System.err.println("Notification failed: " + e.getMessage());
                }
            }
        }

        if (req.getTitle() != null) ticket.setTitle(req.getTitle());
        if (req.getDescription() != null) ticket.setDescription(req.getDescription());
        if (req.getPriority() != null) ticket.setPriority(req.getPriority());
        if (req.getResolutionDetails() != null) ticket.setResolutionDetails(req.getResolutionDetails());
        if (req.getRemarks() != null) ticket.setRemarks(req.getRemarks());
        ticket.setUpdatedBy(updater);

        return mapToResponse(ticketRepository.save(ticket));
    }

    public List<TicketResponse> getAllTickets() {
        return ticketRepository.findAllWithDetails().stream()
            .map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<TicketResponse> getTicketsByUser(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ticketRepository.findByCreatedBy(user).stream()
            .map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<TicketResponse> getTicketsByEmployee(String email) {
        User emp = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        List<EmployeeProjectMapping> mappings =
            empProjectRepository.findByUserAndActiveTrue(emp);

        List<Project> employeeProjects = new ArrayList<>();
        for (EmployeeProjectMapping m : mappings) {
            try {
                Project p = m.getProject();
                if (p != null) employeeProjects.add(p);
            } catch (Exception ignored) {}
        }

        if (employeeProjects.isEmpty()) return new ArrayList<>();

        return ticketRepository.findByProjectIn(employeeProjects).stream()
            .map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<TicketResponse> getTicketsByProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        return ticketRepository.findByProject(project).stream()
            .map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<TicketResponse> searchTickets(
            TicketStatus status, Priority priority,
            Long projectId, Long assignedToId, String search) {
        return ticketRepository.searchTickets(status, priority, projectId, assignedToId, search)
            .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public TicketResponse getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        return mapToResponse(ticket);
    }

    private String generateTicketNumber() {
        String year = DateTimeFormatter.ofPattern("yyyy").format(LocalDateTime.now());
        long count = ticketRepository.count() + 1;
        String num;
        do {
            num = "TKT-" + year + "-" + String.format("%04d", count++);
        } while (ticketRepository.existsByTicketNumber(num));
        return num;
    }

    private void logActivity(Ticket ticket, User user,
                              String action, String old, String newVal, String field) {
        try {
            ActivityLog log = new ActivityLog();
            log.setTicket(ticket);
            log.setPerformedBy(user);
            log.setAction(action);
            log.setOldValue(old);
            log.setNewValue(newVal);
            log.setFieldChanged(field);
            activityLogRepository.save(log);
        } catch (Exception e) {
            System.err.println("Failed to log activity: " + e.getMessage());
        }
    }

    public TicketResponse mapToResponse(Ticket t) {
        try {
            String projectName = null;
            Long projectId = null;
            try {
                Project proj = t.getProject();
                if (proj != null) {
                    projectName = proj.getName();
                    projectId = proj.getId();
                }
            } catch (Exception ignored) {}

            String createdByName = null;
            try {
                User cb = t.getCreatedBy();
                if (cb != null) {
                    createdByName = cb.getFirstName() + " " + cb.getLastName();
                }
            } catch (Exception ignored) {}

            String assignedToName = null;
            Long assignedToId = null;
            try {
                User at = t.getAssignedTo();
                if (at != null) {
                    assignedToName = at.getFirstName() + " " + at.getLastName();
                    assignedToId = at.getId();
                }
            } catch (Exception ignored) {}

            return TicketResponse.builder()
                .id(t.getId())
                .ticketNumber(t.getTicketNumber())
                .title(t.getTitle())
                .description(t.getDescription())
                .category(t.getCategory())
                .priority(t.getPriority())
                .status(t.getStatus())
                .supportLevel(t.getSupportLevel())
                .slaStatus(t.getSlaStatus())
                .slaBreachTime(t.getSlaBreachTime())
                .workingHoursResolution(t.getWorkingHoursResolution())
                .projectId(projectId)
                .projectName(projectName)
                .createdByName(createdByName)
                .assignedToId(assignedToId)
                .assignedToName(assignedToName)
                .resolutionDetails(t.getResolutionDetails())
                .remarks(t.getRemarks())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .resolvedAt(t.getResolvedAt())
                .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to map ticket: " + e.getMessage());
        }
    }

    public List<TicketResponse> getVisibleTickets(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    // Check role
    boolean isAdmin = user.getRoles().stream()
        .anyMatch(r -> r.getName().name().equals("ROLE_SUPER_ADMIN"));
    boolean isEmployee = user.getRoles().stream()
        .anyMatch(r -> r.getName().name().equals("ROLE_SUPPORT_EMPLOYEE"));
    boolean isUser = user.getRoles().stream()
        .anyMatch(r -> r.getName().name().equals("ROLE_USER"));

    if (isAdmin) {
        // Admin sees ALL tickets
        return ticketRepository.findAllWithDetails().stream()
            .map(this::mapToResponse).collect(Collectors.toList());
    }

    if (isEmployee) {
        // Employee sees ONLY tickets from their assigned projects
        List<EmployeeProjectMapping> mappings =
            empProjectRepository.findByUserAndActiveTrue(user);

        List<Project> employeeProjects = new ArrayList<>();
        for (EmployeeProjectMapping m : mappings) {
            try {
                Project p = m.getProject();
                if (p != null) employeeProjects.add(p);
            } catch (Exception ignored) {}
        }

        if (employeeProjects.isEmpty()) return new ArrayList<>();

        return ticketRepository.findByProjectIn(employeeProjects).stream()
            .map(this::mapToResponse).collect(Collectors.toList());
    }

    if (isUser) {
        // Normal user sees ONLY their own tickets
        return ticketRepository.findByCreatedBy(user).stream()
            .map(this::mapToResponse).collect(Collectors.toList());
    }

    return new ArrayList<>();
}
}