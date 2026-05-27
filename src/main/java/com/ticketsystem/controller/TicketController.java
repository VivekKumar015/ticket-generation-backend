package com.ticketsystem.controller;

import com.ticketsystem.dto.request.TicketRequest;
import com.ticketsystem.dto.response.TicketResponse;
import com.ticketsystem.enums.*;
import com.ticketsystem.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(
            @Valid @RequestBody TicketRequest req,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ticketService.createTicket(req, user.getUsername()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketResponse> updateTicket(
            @PathVariable Long id,
            @RequestBody TicketRequest req,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(
            ticketService.updateTicket(id, req, user.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<TicketResponse>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    @GetMapping("/my")
    public ResponseEntity<List<TicketResponse>> getMyTickets(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(
            ticketService.getTicketsByUser(user.getUsername()));
    }

    @GetMapping("/assigned")
    public ResponseEntity<List<TicketResponse>> getAssignedTickets(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(
            ticketService.getTicketsByEmployee(user.getUsername()));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TicketResponse>> getTicketsByProject(
            @PathVariable Long projectId) {
        return ResponseEntity.ok(ticketService.getTicketsByProject(projectId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TicketResponse>> searchTickets(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long assignedToId,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(
            ticketService.searchTickets(status, priority, projectId, assignedToId, search));
    }

    @GetMapping("/my-visible")
    public ResponseEntity<List<TicketResponse>> getVisibleTickets(
        @AuthenticationPrincipal UserDetails userDetails) {
            return ResponseEntity.ok(
        ticketService.getVisibleTickets(userDetails.getUsername()));
}
}