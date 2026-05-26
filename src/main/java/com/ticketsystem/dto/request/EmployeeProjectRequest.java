package com.ticketsystem.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class EmployeeProjectRequest {
    private Long userId;
    private List<Long> projectIds;
    private String roleInProject;
}