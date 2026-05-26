package com.ticketsystem.repository;

import com.ticketsystem.entity.*;
import com.ticketsystem.enums.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface SlaConfigRepository extends JpaRepository<SlaConfiguration, Long> {
    List<SlaConfiguration> findByProject(Project project);
    Optional<SlaConfiguration> findByProjectAndPriority(Project project, Priority priority);
}