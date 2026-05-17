package com.ticketsystem.repository;

import com.ticketsystem.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    Long countByUserAndIsReadFalse(User user);
}