package com.ticketsystem.dto.response;

import com.ticketsystem.enums.*;
import java.time.LocalDateTime;

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

    public Long getId() { return id; }
    public String getTicketNumber() { return ticketNumber; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public Priority getPriority() { return priority; }
    public TicketStatus getStatus() { return status; }
    public SupportLevel getSupportLevel() { return supportLevel; }
    public SlaStatus getSlaStatus() { return slaStatus; }
    public LocalDateTime getSlaBreachTime() { return slaBreachTime; }
    public Double getWorkingHoursResolution() { return workingHoursResolution; }
    public Long getProjectId() { return projectId; }
    public String getProjectName() { return projectName; }
    public String getCreatedByName() { return createdByName; }
    public Long getAssignedToId() { return assignedToId; }
    public String getAssignedToName() { return assignedToName; }
    public String getResolutionDetails() { return resolutionDetails; }
    public String getRemarks() { return remarks; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private TicketResponse r = new TicketResponse();
        public Builder id(Long v) { r.id = v; return this; }
        public Builder ticketNumber(String v) { r.ticketNumber = v; return this; }
        public Builder title(String v) { r.title = v; return this; }
        public Builder description(String v) { r.description = v; return this; }
        public Builder category(String v) { r.category = v; return this; }
        public Builder priority(Priority v) { r.priority = v; return this; }
        public Builder status(TicketStatus v) { r.status = v; return this; }
        public Builder supportLevel(SupportLevel v) { r.supportLevel = v; return this; }
        public Builder slaStatus(SlaStatus v) { r.slaStatus = v; return this; }
        public Builder slaBreachTime(LocalDateTime v) { r.slaBreachTime = v; return this; }
        public Builder workingHoursResolution(Double v) { r.workingHoursResolution = v; return this; }
        public Builder projectId(Long v) { r.projectId = v; return this; }
        public Builder projectName(String v) { r.projectName = v; return this; }
        public Builder createdByName(String v) { r.createdByName = v; return this; }
        public Builder assignedToId(Long v) { r.assignedToId = v; return this; }
        public Builder assignedToName(String v) { r.assignedToName = v; return this; }
        public Builder resolutionDetails(String v) { r.resolutionDetails = v; return this; }
        public Builder remarks(String v) { r.remarks = v; return this; }
        public Builder createdAt(LocalDateTime v) { r.createdAt = v; return this; }
        public Builder updatedAt(LocalDateTime v) { r.updatedAt = v; return this; }
        public Builder resolvedAt(LocalDateTime v) { r.resolvedAt = v; return this; }
        public TicketResponse build() { return r; }
    }
}