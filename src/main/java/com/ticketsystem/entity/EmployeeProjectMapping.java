package com.ticketsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee_project_mappings",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "project_id"}))
public class EmployeeProjectMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    private String roleInProject;
    private Boolean active = true;
    private LocalDateTime assignedAt;

    @PrePersist
    protected void onCreate() { assignedAt = LocalDateTime.now(); }

    // ===== GETTERS =====
    public Long getId() { return id; }
    public User getUser() { return user; }
    public Project getProject() { return project; }
    public String getRoleInProject() { return roleInProject; }
    public Boolean getActive() { return active; }
    public LocalDateTime getAssignedAt() { return assignedAt; }

    // ===== SETTERS =====
    public void setUser(User user) { this.user = user; }
    public void setProject(Project project) { this.project = project; }
    public void setRoleInProject(String roleInProject) { this.roleInProject = roleInProject; }
    public void setActive(Boolean active) { this.active = active; }
}