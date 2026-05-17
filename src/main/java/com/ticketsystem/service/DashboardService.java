package com.ticketsystem.service;

import com.ticketsystem.dto.response.DashboardResponse;
import com.ticketsystem.enums.*;
import com.ticketsystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public DashboardResponse getAdminDashboard() {
        return DashboardResponse.builder()
            .totalTickets(ticketRepository.count())
            .openTickets(ticketRepository.countByStatus(TicketStatus.OPEN))
            .inProgressTickets(ticketRepository.countByStatus(TicketStatus.IN_PROGRESS))
            .pendingTickets(ticketRepository.countByStatus(TicketStatus.PENDING))
            .resolvedTickets(ticketRepository.countByStatus(TicketStatus.RESOLVED))
            .closedTickets(ticketRepository.countByStatus(TicketStatus.CLOSED))
            .criticalTickets(ticketRepository.countByPriority(Priority.P1_CRITICAL))
            .totalUsers(userRepository.count())
            .totalEmployees((long) userRepository.findByRoleName(RoleName.ROLE_SUPPORT_EMPLOYEE).size())
            .build();
    }
}