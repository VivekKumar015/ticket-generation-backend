package com.ticketsystem.controller;

import com.ticketsystem.dto.request.CommentRequest;
import com.ticketsystem.entity.Comment;
import com.ticketsystem.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tickets/{ticketId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Comment> addComment(
            @PathVariable Long ticketId,
            @RequestBody CommentRequest req,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(commentService.addComment(ticketId, req, user.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long ticketId) {
        return ResponseEntity.ok(commentService.getCommentsByTicket(ticketId));
    }
}