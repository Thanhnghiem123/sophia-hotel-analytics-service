package vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses;

import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
public class ReportResponse {
    private UUID id;
    private String reportType;
    private String data;
    private Timestamp createdAt;
}
