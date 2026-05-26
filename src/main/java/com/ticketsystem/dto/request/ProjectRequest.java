package com.ticketsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjectRequest {
    @NotBlank
    private String name;
    private String projectCode;
    private String description;
    private String supportEmail;
    private Integer slaHours;
    private Long shiftId;
    private Boolean active;
}