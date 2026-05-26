package com.ticketsystem.repository;

import com.ticketsystem.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface EmployeeProjectRepository extends JpaRepository<EmployeeProjectMapping, Long> {

    // Get all projects for an employee
    @Query("SELECT m FROM EmployeeProjectMapping m " +
           "LEFT JOIN FETCH m.project p " +
           "WHERE m.user = :user AND m.active = true")
    List<EmployeeProjectMapping> findByUserAndActiveTrue(
        @Param("user") User user);

    // Get all employees for a project
    @Query("SELECT m FROM EmployeeProjectMapping m " +
           "LEFT JOIN FETCH m.user u " +
           "WHERE m.project = :project AND m.active = true")
    List<EmployeeProjectMapping> findByProjectAndActiveTrue(
        @Param("project") Project project);

    // Check if employee belongs to project
    @Query("SELECT COUNT(m) > 0 FROM EmployeeProjectMapping m " +
           "WHERE m.user = :user AND m.project = :project AND m.active = true")
    Boolean existsByUserAndProject(
        @Param("user") User user,
        @Param("project") Project project);

    // Get mapping by user and project
    java.util.Optional<EmployeeProjectMapping> findByUserAndProject(
        User user, Project project);
}