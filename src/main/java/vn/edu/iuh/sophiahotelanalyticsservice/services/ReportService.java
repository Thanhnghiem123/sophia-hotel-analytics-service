package vn.edu.iuh.sophiahotelanalyticsservice.services;

import vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses.ReportResponse;

import java.util.List;
import java.util.UUID;

public interface ReportService {
    List<ReportResponse> getAllReports();
    ReportResponse getReportById(UUID id);
}
