package com.ticketsystem.repository;

import com.ticketsystem.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByTicketOrderByCreatedAtDesc(Ticket ticket);
}
