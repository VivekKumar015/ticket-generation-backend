package com.ticketsystem.controller;

import com.ticketsystem.dto.response.ReportResponse;
import com.ticketsystem.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/overall")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> getOverallReport() {
        return ResponseEntity.ok(reportService.getOverallReport());
    }

    @GetMapping("/projects")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<ReportResponse>> getProjectWiseReport() {
        return ResponseEntity.ok(reportService.getProjectWiseReport());
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<ReportResponse> getProjectReport(
            @PathVariable Long projectId) {
        return ResponseEntity.ok(reportService.getProjectReport(projectId));
    }
}