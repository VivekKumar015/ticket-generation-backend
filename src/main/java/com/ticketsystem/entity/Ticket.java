package com.ticketsystem.entity;

import com.ticketsystem.enums.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String ticketNumber;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String category;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Enumerated(EnumType.STRING)
    private SupportLevel supportLevel;

    @Enumerated(EnumType.STRING)
    private SlaStatus slaStatus = SlaStatus.WITHIN_SLA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_id")
    private User updatedBy;

    @Column(columnDefinition = "TEXT")
    private String resolutionDetails;

    private String remarks;
    private LocalDateTime slaBreachTime;
    private Double workingHoursResolution;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    private LocalDateTime responseAt;
    private LocalDateTime resolvedAt;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Attachment> attachments = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = TicketStatus.OPEN;
        if (slaStatus == null) slaStatus = SlaStatus.WITHIN_SLA;
    }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    // ===== GETTERS =====
    public Long getId() { return id; }
    public String getTicketNumber() { return ticketNumber; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public Priority getPriority() { return priority; }
    public TicketStatus getStatus() { return status; }
    public SupportLevel getSupportLevel() { return supportLevel; }
    public SlaStatus getSlaStatus() { return slaStatus; }
    public Project getProject() { return project; }
    public User getCreatedBy() { return createdBy; }
    public User getAssignedTo() { return assignedTo; }
    public User getUpdatedBy() { return updatedBy; }
    public String getResolutionDetails() { return resolutionDetails; }
    public String getRemarks() { return remarks; }
    public LocalDateTime getSlaBreachTime() { return slaBreachTime; }
    public Double getWorkingHoursResolution() { return workingHoursResolution; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public List<Comment> getComments() { return comments; }
    public List<Attachment> getAttachments() { return attachments; }

    // ===== SETTERS =====
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setCategory(String category) { this.category = category; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public void setStatus(TicketStatus status) { this.status = status; }
    public void setSupportLevel(SupportLevel supportLevel) { this.supportLevel = supportLevel; }
    public void setSlaStatus(SlaStatus slaStatus) { this.slaStatus = slaStatus; }
    public void setProject(Project project) { this.project = project; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    public void setAssignedTo(User assignedTo) { this.assignedTo = assignedTo; }
    public void setUpdatedBy(User updatedBy) { this.updatedBy = updatedBy; }
    public void setResolutionDetails(String resolutionDetails) { this.resolutionDetails = resolutionDetails; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public void setSlaBreachTime(LocalDateTime slaBreachTime) { this.slaBreachTime = slaBreachTime; }
    public void setWorkingHoursResolution(Double workingHoursResolution) { this.workingHoursResolution = workingHoursResolution; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
    public void setAttachments(List<Attachment> attachments) { this.attachments = attachments; }
}