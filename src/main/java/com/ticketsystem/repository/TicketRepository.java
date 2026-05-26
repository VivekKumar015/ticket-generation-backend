package com.ticketsystem.repository;

import com.ticketsystem.entity.*;
import com.ticketsystem.enums.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.createdBy LEFT JOIN FETCH t.assignedTo LEFT JOIN FETCH t.project p LEFT JOIN FETCH p.shift WHERE t.createdBy = :user")
    List<Ticket> findByCreatedBy(@Param("user") User user);

    @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.createdBy LEFT JOIN FETCH t.assignedTo LEFT JOIN FETCH t.project p LEFT JOIN FETCH p.shift WHERE t.assignedTo = :employee")
    List<Ticket> findByAssignedTo(@Param("employee") User employee);

    @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.createdBy LEFT JOIN FETCH t.assignedTo LEFT JOIN FETCH t.project p LEFT JOIN FETCH p.shift WHERE t.project = :project")
    List<Ticket> findByProject(@Param("project") Project project);

    @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.createdBy LEFT JOIN FETCH t.assignedTo LEFT JOIN FETCH t.project p LEFT JOIN FETCH p.shift WHERE t.project IN :projects")
    List<Ticket> findByProjectIn(@Param("projects") List<Project> projects);

    @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.createdBy LEFT JOIN FETCH t.assignedTo LEFT JOIN FETCH t.project p LEFT JOIN FETCH p.shift")
    List<Ticket> findAllWithDetails();

    Long countByStatus(TicketStatus status);
    Long countByPriority(Priority priority);
    Long countByProject(Project project);
    Long countByProjectAndStatus(Project project, TicketStatus status);
    Long countByProjectAndPriority(Project project, Priority priority);
    Long countByAssignedTo(User employee);

    @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.createdBy LEFT JOIN FETCH t.assignedTo LEFT JOIN FETCH t.project p LEFT JOIN FETCH p.shift WHERE (:status IS NULL OR t.status = :status) AND (:priority IS NULL OR t.priority = :priority) AND (:projectId IS NULL OR t.project.id = :projectId) AND (:assignedToId IS NULL OR t.assignedTo.id = :assignedToId) AND (:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%',:search,'%')) OR LOWER(t.ticketNumber) LIKE LOWER(CONCAT('%',:search,'%')))")
    List<Ticket> searchTickets(
        @Param("status") TicketStatus status,
        @Param("priority") Priority priority,
        @Param("projectId") Long projectId,
        @Param("assignedToId") Long assignedToId,
        @Param("search") String search
    );

    Boolean existsByTicketNumber(String ticketNumber);
}