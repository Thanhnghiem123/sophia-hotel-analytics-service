package vn.edu.iuh.sophiahotelanalyticsservice.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DateRangeRequest {
    private LocalDate from;
    private LocalDate to;
    private String groupBy; // 'day', 'month', 'year'
    private String hotelId; // optional
}
