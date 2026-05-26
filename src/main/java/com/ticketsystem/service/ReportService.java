package com.ticketsystem.service;

import com.ticketsystem.dto.response.ReportResponse;
import com.ticketsystem.entity.*;
import com.ticketsystem.enums.*;
import com.ticketsystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TicketRepository ticketRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final SlaService slaService;

    public List<ReportResponse> getProjectWiseReport() {
        return projectRepository.findAll().stream()
            .map(this::buildProjectReport)
            .collect(Collectors.toList());
    }

    public ReportResponse getProjectReport(Long projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found"));
        return buildProjectReport(project);
    }

    public Map<String, Object> getOverallReport() {
        Map<String, Object> report = new LinkedHashMap<>();

        List<Ticket> allTickets = ticketRepository.findAllWithDetails();

        report.put("totalTickets", allTickets.size());
        report.put("openTickets", allTickets.stream()
            .filter(t -> t.getStatus() == TicketStatus.OPEN).count());
        report.put("resolvedTickets", allTickets.stream()
            .filter(t -> t.getStatus() == TicketStatus.RESOLVED).count());
        report.put("closedTickets", allTickets.stream()
            .filter(t -> t.getStatus() == TicketStatus.CLOSED).count());
        report.put("criticalTickets", allTickets.stream()
            .filter(t -> t.getPriority() == Priority.P1_CRITICAL).count());
        report.put("slaBreachedTickets", allTickets.stream()
            .filter(t -> t.getSlaStatus() == SlaStatus.SLA_BREACHED).count());

        // Average resolution hours
        OptionalDouble avgResolution = allTickets.stream()
            .filter(t -> t.getWorkingHoursResolution() != null)
            .mapToDouble(Ticket::getWorkingHoursResolution)
            .average();
        report.put("avgResolutionHours", avgResolution.isPresent()
            ? Math.round(avgResolution.getAsDouble() * 100.0) / 100.0 : 0);

        // Status breakdown
        Map<String, Long> statusBreakdown = new LinkedHashMap<>();
        for (TicketStatus s : TicketStatus.values()) {
            statusBreakdown.put(s.name(), allTickets.stream()
                .filter(t -> t.getStatus() == s).count());
        }
        report.put("statusBreakdown", statusBreakdown);

        // Priority breakdown
        Map<String, Long> priorityBreakdown = new LinkedHashMap<>();
        for (Priority p : Priority.values()) {
            priorityBreakdown.put(p.name(), allTickets.stream()
                .filter(t -> t.getPriority() == p).count());
        }
        report.put("priorityBreakdown", priorityBreakdown);

        // Project breakdown
        List<Map<String, Object>> projectBreakdown = projectRepository.findAll().stream()
            .map(proj -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("project", proj.getName());
                m.put("total", ticketRepository.countByProject(proj));
                m.put("open", ticketRepository.countByProjectAndStatus(proj, TicketStatus.OPEN));
                m.put("resolved", ticketRepository.countByProjectAndStatus(proj, TicketStatus.RESOLVED));
                return m;
            }).collect(Collectors.toList());
        report.put("projectBreakdown", projectBreakdown);

        return report;
    }

    private ReportResponse buildProjectReport(Project project) {
        List<Ticket> tickets = ticketRepository.findByProject(project);

        long total = tickets.size();
        long open = tickets.stream().filter(t -> t.getStatus() == TicketStatus.OPEN).count();
        long resolved = tickets.stream().filter(t -> t.getStatus() == TicketStatus.RESOLVED).count();
        long pending = tickets.stream().filter(t -> t.getStatus() == TicketStatus.PENDING).count();
        long critical = tickets.stream().filter(t -> t.getPriority() == Priority.P1_CRITICAL).count();
        long slaBreached = tickets.stream().filter(t -> t.getSlaStatus() == SlaStatus.SLA_BREACHED).count();

        OptionalDouble avgResolution = tickets.stream()
            .filter(t -> t.getWorkingHoursResolution() != null)
            .mapToDouble(Ticket::getWorkingHoursResolution)
            .average();

        // Priority breakdown
        Map<String, Long> priorityBreakdown = new LinkedHashMap<>();
        for (Priority p : Priority.values()) {
            priorityBreakdown.put(p.name(), tickets.stream().filter(t -> t.getPriority() == p).count());
        }

        // Status breakdown
        Map<String, Long> statusBreakdown = new LinkedHashMap<>();
        for (TicketStatus s : TicketStatus.values()) {
            statusBreakdown.put(s.name(), tickets.stream().filter(t -> t.getStatus() == s).count());
        }

        // Employee performance
        Map<User, List<Ticket>> byEmployee = tickets.stream()
            .filter(t -> t.getAssignedTo() != null)
            .collect(Collectors.groupingBy(Ticket::getAssignedTo));

        List<Map<String, Object>> empPerformance = byEmployee.entrySet().stream()
            .map(e -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("employeeName", e.getKey().getFirstName() + " " + e.getKey().getLastName());
                m.put("totalAssigned", e.getValue().size());
                m.put("resolved", e.getValue().stream()
                    .filter(t -> t.getStatus() == TicketStatus.RESOLVED).count());
                OptionalDouble avg = e.getValue().stream()
                    .filter(t -> t.getWorkingHoursResolution() != null)
                    .mapToDouble(Ticket::getWorkingHoursResolution).average();
                m.put("avgResolutionHours", avg.isPresent() ? avg.getAsDouble() : 0);
                return m;
            }).collect(Collectors.toList());

        return ReportResponse.builder()
            .projectName(project.getName())
            .totalTickets(total).openTickets(open).resolvedTickets(resolved)
            .pendingTickets(pending).criticalTickets(critical)
            .slaBreachedTickets(slaBreached)
            .avgResolutionHours(avgResolution.isPresent() ? avgResolution.getAsDouble() : 0.0)
            .priorityBreakdown(priorityBreakdown)
            .statusBreakdown(statusBreakdown)
            .employeePerformance(empPerformance)
            .build();
    }
}