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
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final NotificationService notificationService;
    private final ActivityLogRepository activityLogRepository;

    @Transactional
    public TicketResponse createTicket(TicketRequest req, String userEmail) {
        User creator = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Ticket ticket = Ticket.builder()
            .ticketNumber(generateTicketNumber())
            .title(req.getTitle())
            .description(req.getDescription())
            .category(req.getCategory())
            .priority(req.getPriority() != null ? req.getPriority() : Priority.P3_MEDIUM)
            .supportLevel(req.getSupportLevel() != null ? req.getSupportLevel() : SupportLevel.L1)
            .status(TicketStatus.OPEN)
            .createdBy(creator)
            .build();

        if (req.getProjectId() != null) {
            Project project = projectRepository.findById(req.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
            ticket.setProject(project);
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
                ticket.getStatus().name(), req.getStatus().name(), "status");

            if (req.getStatus() == TicketStatus.RESOLVED) {
                ticket.setResolvedAt(LocalDateTime.now());
            }
            ticket.setStatus(req.getStatus());

            notificationService.sendNotification(ticket.getCreatedBy(),
                "Ticket " + ticket.getTicketNumber() + " status updated to: " + req.getStatus(),
                "STATUS_UPDATE", ticket);
        }

        if (req.getAssignedToId() != null) {
            User assignee = userRepository.findById(req.getAssignedToId()).orElse(null);
            ticket.setAssignedTo(assignee);
            if (assignee != null) {
                notificationService.sendNotification(assignee,
                    "Ticket " + ticket.getTicketNumber() + " assigned to you",
                    "ASSIGNED", ticket);
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
        return ticketRepository.findAll().stream()
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
        return ticketRepository.findByAssignedTo(emp).stream()
            .map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<TicketResponse> searchTickets(TicketStatus status, Priority priority,
                                                Long assignedToId, String search) {
        return ticketRepository.searchTickets(status, priority, assignedToId, search)
            .stream().map(this::mapToResponse).collect(Collectors.toList());
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
        activityLogRepository.save(ActivityLog.builder()
            .ticket(ticket).performedBy(user)
            .action(action).oldValue(old).newValue(newVal).fieldChanged(field)
            .build());
    }

    private TicketResponse mapToResponse(Ticket t) {
        return TicketResponse.builder()
            .id(t.getId()).ticketNumber(t.getTicketNumber())
            .title(t.getTitle()).description(t.getDescription())
            .category(t.getCategory()).priority(t.getPriority())
            .status(t.getStatus()).supportLevel(t.getSupportLevel())
            .projectName(t.getProject() != null ? t.getProject().getName() : null)
            .createdByName(t.getCreatedBy().getFirstName() + " " + t.getCreatedBy().getLastName())
            .assignedToName(t.getAssignedTo() != null
                ? t.getAssignedTo().getFirstName() + " " + t.getAssignedTo().getLastName() : null)
            .resolutionDetails(t.getResolutionDetails())
            .remarks(t.getRemarks())
            .createdAt(t.getCreatedAt()).updatedAt(t.getUpdatedAt())
            .resolvedAt(t.getResolvedAt()).build();
    }
}