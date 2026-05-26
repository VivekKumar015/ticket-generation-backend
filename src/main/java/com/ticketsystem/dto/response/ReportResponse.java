package com.ticketsystem.dto.response;

import lombok.*;
import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponse {
    // Project-wise stats
    private String projectName;
    private Long totalTickets;
    private Long openTickets;
    private Long resolvedTickets;
    private Long pendingTickets;
    private Long criticalTickets;
    private Long slaBreachedTickets;
    private Double avgResolutionHours;

    // Employee performance
    private List<Map<String, Object>> employeePerformance;

    // Priority breakdown
    private Map<String, Long> priorityBreakdown;

    // Status breakdown
    private Map<String, Long> statusBreakdown;
}
