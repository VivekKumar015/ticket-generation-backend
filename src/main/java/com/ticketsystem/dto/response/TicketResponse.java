package com.ticketsystem.dto.response;

import com.ticketsystem.enums.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketResponse {
    private Long id;
    private String ticketNumber;
    private String title;
    private String description;
    private String category;
    private Priority priority;
    private TicketStatus status;
    private SupportLevel supportLevel;
    private SlaStatus slaStatus;
    private LocalDateTime slaBreachTime;
    private Double workingHoursResolution;
    private Long projectId;
    private String projectName;
    private String createdByName;
    private Long assignedToId;
    private String assignedToName;
    private String resolutionDetails;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
}