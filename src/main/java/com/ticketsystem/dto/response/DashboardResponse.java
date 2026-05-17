package com.ticketsystem.dto.response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {
    private Long totalTickets;
    private Long openTickets;
    private Long inProgressTickets;
    private Long pendingTickets;
    private Long resolvedTickets;
    private Long closedTickets;
    private Long criticalTickets;
    private Long totalUsers;
    private Long totalEmployees;
}
