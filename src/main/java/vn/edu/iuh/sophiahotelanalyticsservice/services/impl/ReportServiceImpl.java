package vn.edu.iuh.sophiahotelanalyticsservice.services.impl;

import org.springframework.stereotype.Service;
import vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses.ReportResponse;
import vn.edu.iuh.sophiahotelanalyticsservice.exceptions.NotFoundException;
import vn.edu.iuh.sophiahotelanalyticsservice.mappers.ReportMapper;
import vn.edu.iuh.sophiahotelanalyticsservice.repositories.ReportRepository;
import vn.edu.iuh.sophiahotelanalyticsservice.services.ReportService;

import java.util.List;
import java.util.UUID;

@Service
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;

    public ReportServiceImpl(ReportRepository reportRepository, ReportMapper reportMapper) {
        this.reportRepository = reportRepository;
        this.reportMapper = reportMapper;
    }

    @Override
    public List<ReportResponse> getAllReports() {
        return reportRepository.findAll().stream().map(reportMapper::toDto).toList();
    }

    @Override
    public ReportResponse getReportById(UUID id) {
        return reportRepository.findById(id)
                .map(reportMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Report not found"));
    }
}
