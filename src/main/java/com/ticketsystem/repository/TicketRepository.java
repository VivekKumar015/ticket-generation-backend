package com.ticketsystem.repository;

import com.ticketsystem.entity.*;
import com.ticketsystem.enums.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByCreatedBy(User user);

    List<Ticket> findByAssignedTo(User employee);

    List<Ticket> findByStatus(TicketStatus status);

    List<Ticket> findByPriority(Priority priority);

    Long countByStatus(TicketStatus status);

    Long countByPriority(Priority priority);

    Long countByAssignedTo(User employee);

    @Query("SELECT t FROM Ticket t WHERE " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority) AND " +
           "(:assignedToId IS NULL OR t.assignedTo.id = :assignedToId) AND " +
           "(:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%',:search,'%')) " +
           " OR LOWER(t.ticketNumber) LIKE LOWER(CONCAT('%',:search,'%')))")
    List<Ticket> searchTickets(
        @Param("status") TicketStatus status,
        @Param("priority") Priority priority,
        @Param("assignedToId") Long assignedToId,
        @Param("search") String search
    );

    Boolean existsByTicketNumber(String ticketNumber);
}