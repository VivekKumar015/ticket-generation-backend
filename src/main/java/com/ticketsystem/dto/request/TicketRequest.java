package com.ticketsystem.dto.request;

import com.ticketsystem.enums.*;

public class TicketRequest {

    private String title;
    private String description;
    private String category;
    private Priority priority;
    private SupportLevel supportLevel;
    private Long projectId;
    private Long assignedToId;
    private TicketStatus status;
    private String resolutionDetails;
    private String remarks;

    // ===== GETTERS =====
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public Priority getPriority() { return priority; }
    public SupportLevel getSupportLevel() { return supportLevel; }
    public Long getProjectId() { return projectId; }
    public Long getAssignedToId() { return assignedToId; }
    public TicketStatus getStatus() { return status; }
    public String getResolutionDetails() { return resolutionDetails; }
    public String getRemarks() { return remarks; }

    // ===== SETTERS =====
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setCategory(String category) { this.category = category; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public void setSupportLevel(SupportLevel supportLevel) { this.supportLevel = supportLevel; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public void setAssignedToId(Long assignedToId) { this.assignedToId = assignedToId; }
    public void setStatus(TicketStatus status) { this.status = status; }
    public void setResolutionDetails(String resolutionDetails) { this.resolutionDetails = resolutionDetails; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}