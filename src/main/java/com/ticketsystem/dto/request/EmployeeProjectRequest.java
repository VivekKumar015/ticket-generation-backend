package com.ticketsystem.dto.request;

import java.util.List;

public class EmployeeProjectRequest {
    private Long userId;
    private List<Long> projectIds;
    private String roleInProject;

    public Long getUserId() { return userId; }
    public List<Long> getProjectIds() { return projectIds; }
    public String getRoleInProject() { return roleInProject; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setProjectIds(List<Long> projectIds) { this.projectIds = projectIds; }
    public void setRoleInProject(String roleInProject) { this.roleInProject = roleInProject; }
}