package vn.edu.iuh.sophiahotelanalyticsservice.controllers;

import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses.ReportResponse;
import vn.edu.iuh.sophiahotelanalyticsservice.services.ReportService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reports")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public List<ReportResponse> getAllReports() {
        return reportService.getAllReports();
    }

    @GetMapping("/{id}")
    public ReportResponse getReportById(@PathVariable UUID id) {
        return reportService.getReportById(id);
    }
}
