package com.ticketsystem.dto.response;

import java.time.LocalDateTime;

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

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getProjectCode() { return projectCode; }
    public String getDescription() { return description; }
    public String getSupportEmail() { return supportEmail; }
    public Integer getSlaHours() { return slaHours; }
    public String getShiftName() { return shiftName; }
    public String getShiftStartTime() { return shiftStartTime; }
    public String getShiftEndTime() { return shiftEndTime; }
    public Boolean getActive() { return active; }
    public Long getTotalTickets() { return totalTickets; }
    public Long getOpenTickets() { return openTickets; }
    public Long getResolvedTickets() { return resolvedTickets; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private ProjectResponse r = new ProjectResponse();
        public Builder id(Long v) { r.id = v; return this; }
        public Builder name(String v) { r.name = v; return this; }
        public Builder projectCode(String v) { r.projectCode = v; return this; }
        public Builder description(String v) { r.description = v; return this; }
        public Builder supportEmail(String v) { r.supportEmail = v; return this; }
        public Builder slaHours(Integer v) { r.slaHours = v; return this; }
        public Builder shiftName(String v) { r.shiftName = v; return this; }
        public Builder shiftStartTime(String v) { r.shiftStartTime = v; return this; }
        public Builder shiftEndTime(String v) { r.shiftEndTime = v; return this; }
        public Builder active(Boolean v) { r.active = v; return this; }
        public Builder totalTickets(Long v) { r.totalTickets = v; return this; }
        public Builder openTickets(Long v) { r.openTickets = v; return this; }
        public Builder resolvedTickets(Long v) { r.resolvedTickets = v; return this; }
        public Builder createdAt(LocalDateTime v) { r.createdAt = v; return this; }
        public ProjectResponse build() { return r; }
    }
}