package com.ticketsystem.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponse {
    private Long id;
    private String name;
    private String projectCode;
    private String description;
    private String supportEmail;
    private Integer slaHours;
    private String shiftName;
    private String shiftStartTime;
    private String shiftEndTime;
    private Boolean active;
    private Long totalTickets;
    private Long openTickets;
    private Long resolvedTickets;
    private LocalDateTime createdAt;
}