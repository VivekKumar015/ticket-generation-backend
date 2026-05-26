package com.ticketsystem.dto.response;

import java.util.List;
import java.util.Map;

public class ReportResponse {
    private String projectName;
    private Long totalTickets;
    private Long openTickets;
    private Long resolvedTickets;
    private Long pendingTickets;
    private Long criticalTickets;
    private Long slaBreachedTickets;
    private Double avgResolutionHours;
    private List<Map<String, Object>> employeePerformance;
    private Map<String, Long> priorityBreakdown;
    private Map<String, Long> statusBreakdown;

    public String getProjectName() { return projectName; }
    public Long getTotalTickets() { return totalTickets; }
    public Long getOpenTickets() { return openTickets; }
    public Long getResolvedTickets() { return resolvedTickets; }
    public Long getPendingTickets() { return pendingTickets; }
    public Long getCriticalTickets() { return criticalTickets; }
    public Long getSlaBreachedTickets() { return slaBreachedTickets; }
    public Double getAvgResolutionHours() { return avgResolutionHours; }
    public List<Map<String, Object>> getEmployeePerformance() { return employeePerformance; }
    public Map<String, Long> getPriorityBreakdown() { return priorityBreakdown; }
    public Map<String, Long> getStatusBreakdown() { return statusBreakdown; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private ReportResponse r = new ReportResponse();
        public Builder projectName(String v) { r.projectName = v; return this; }
        public Builder totalTickets(Long v) { r.totalTickets = v; return this; }
        public Builder openTickets(Long v) { r.openTickets = v; return this; }
        public Builder resolvedTickets(Long v) { r.resolvedTickets = v; return this; }
        public Builder pendingTickets(Long v) { r.pendingTickets = v; return this; }
        public Builder criticalTickets(Long v) { r.criticalTickets = v; return this; }
        public Builder slaBreachedTickets(Long v) { r.slaBreachedTickets = v; return this; }
        public Builder avgResolutionHours(Double v) { r.avgResolutionHours = v; return this; }
        public Builder employeePerformance(List<Map<String, Object>> v) { r.employeePerformance = v; return this; }
        public Builder priorityBreakdown(Map<String, Long> v) { r.priorityBreakdown = v; return this; }
        public Builder statusBreakdown(Map<String, Long> v) { r.statusBreakdown = v; return this; }
        public ReportResponse build() { return r; }
    }
}