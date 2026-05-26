package com.ticketsystem.dto.response;

public class DashboardResponse {
    private Long totalTickets;
    private Long openTickets;
    private Long inProgressTickets;
    private Long pendingTickets;
    private Long resolvedTickets;
    private Long closedTickets;
    private Long criticalTickets;
    private Long totalUsers;
    private Long totalEmployees;

    public Long getTotalTickets() { return totalTickets; }
    public Long getOpenTickets() { return openTickets; }
    public Long getInProgressTickets() { return inProgressTickets; }
    public Long getPendingTickets() { return pendingTickets; }
    public Long getResolvedTickets() { return resolvedTickets; }
    public Long getClosedTickets() { return closedTickets; }
    public Long getCriticalTickets() { return criticalTickets; }
    public Long getTotalUsers() { return totalUsers; }
    public Long getTotalEmployees() { return totalEmployees; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private DashboardResponse r = new DashboardResponse();
        public Builder totalTickets(Long v) { r.totalTickets = v; return this; }
        public Builder openTickets(Long v) { r.openTickets = v; return this; }
        public Builder inProgressTickets(Long v) { r.inProgressTickets = v; return this; }
        public Builder pendingTickets(Long v) { r.pendingTickets = v; return this; }
        public Builder resolvedTickets(Long v) { r.resolvedTickets = v; return this; }
        public Builder closedTickets(Long v) { r.closedTickets = v; return this; }
        public Builder criticalTickets(Long v) { r.criticalTickets = v; return this; }
        public Builder totalUsers(Long v) { r.totalUsers = v; return this; }
        public Builder totalEmployees(Long v) { r.totalEmployees = v; return this; }
        public DashboardResponse build() { return r; }
    }
}