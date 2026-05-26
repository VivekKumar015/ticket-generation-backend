package com.ticketsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String projectCode;
    private String description;
    private String supportEmail;
    private Integer slaHours = 24;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shift_id")
    private Shift shift;

    private Boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    // GETTERS
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getProjectCode() { return projectCode; }
    public String getDescription() { return description; }
    public String getSupportEmail() { return supportEmail; }
    public Integer getSlaHours() { return slaHours; }
    public Shift getShift() { return shift; }
    public Boolean getActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // SETTERS
    public void setName(String name) { this.name = name; }
    public void setProjectCode(String projectCode) { this.projectCode = projectCode; }
    public void setDescription(String description) { this.description = description; }
    public void setSupportEmail(String supportEmail) { this.supportEmail = supportEmail; }
    public void setSlaHours(Integer slaHours) { this.slaHours = slaHours; }
    public void setShift(Shift shift) { this.shift = shift; }
    public void setActive(Boolean active) { this.active = active; }
}