package com.ticketsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShiftRequest {
    @NotBlank
    private String name;
    @NotNull
    private String startTime;  // "09:00"
    @NotNull
    private String endTime;    // "18:00"
    private String timezone;
}