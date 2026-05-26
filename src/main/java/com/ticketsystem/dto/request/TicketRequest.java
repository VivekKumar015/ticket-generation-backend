package com.ticketsystem.dto.request;

import com.ticketsystem.enums.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    private String category;
    private Priority priority;
    private SupportLevel supportLevel;
    private Long projectId;
    private Long assignedToId;
    private TicketStatus status;
    private String resolutionDetails;
    private String remarks;
}