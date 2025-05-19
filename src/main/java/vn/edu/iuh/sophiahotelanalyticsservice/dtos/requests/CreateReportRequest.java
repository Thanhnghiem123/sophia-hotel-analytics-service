package vn.edu.iuh.sophiahotelanalyticsservice.dtos.requests;

import lombok.Data;

@Data
public class CreateReportRequest {
    private String reportType;
    private String data;
}
