package com.ticketsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    // GETTERS
    public Long getId() { return id; }
    public String getContent() { return content; }
    public Ticket getTicket() { return ticket; }
    public User getAuthor() { return author; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // SETTERS
    public void setContent(String content) { this.content = content; }
    public void setTicket(Ticket ticket) { this.ticket = ticket; }
    public void setAuthor(User author) { this.author = author; }
}