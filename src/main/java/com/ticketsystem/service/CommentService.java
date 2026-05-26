package com.ticketsystem.service;

import com.ticketsystem.dto.request.CommentRequest;
import com.ticketsystem.entity.*;
import com.ticketsystem.exception.ResourceNotFoundException;
import com.ticketsystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public Comment addComment(Long ticketId, CommentRequest req, String userEmail) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Comment comment = new Comment();
        comment.setContent(req.getContent());
        comment.setTicket(ticket);
        comment.setAuthor(user);
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        return commentRepository.findByTicketOrderByCreatedAtAsc(ticket);
    }
}