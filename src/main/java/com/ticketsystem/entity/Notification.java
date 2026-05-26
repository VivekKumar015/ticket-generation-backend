package com.ticketsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;
    private String type;
    private Boolean isRead = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    // GETTERS
    public Long getId() { return id; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public Boolean getIsRead() { return isRead; }
    public User getUser() { return user; }
    public Ticket getTicket() { return ticket; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // SETTERS
    public void setMessage(String message) { this.message = message; }
    public void setType(String type) { this.type = type; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    public void setUser(User user) { this.user = user; }
    public void setTicket(Ticket ticket) { this.ticket = ticket; }
}