package com.ticketsystem.dto.request;

public class ProjectRequest {
    private String name;
    private String projectCode;
    private String description;
    private String supportEmail;
    private Integer slaHours;
    private Long shiftId;
    private Boolean active;

    public String getName() { return name; }
    public String getProjectCode() { return projectCode; }
    public String getDescription() { return description; }
    public String getSupportEmail() { return supportEmail; }
    public Integer getSlaHours() { return slaHours; }
    public Long getShiftId() { return shiftId; }
    public Boolean getActive() { return active; }
    public void setName(String name) { this.name = name; }
    public void setProjectCode(String projectCode) { this.projectCode = projectCode; }
    public void setDescription(String description) { this.description = description; }
    public void setSupportEmail(String supportEmail) { this.supportEmail = supportEmail; }
    public void setSlaHours(Integer slaHours) { this.slaHours = slaHours; }
    public void setShiftId(Long shiftId) { this.shiftId = shiftId; }
    public void setActive(Boolean active) { this.active = active; }
}