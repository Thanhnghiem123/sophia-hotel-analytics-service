package vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerStatisticsResponse {
    private LocalDate date;
    private String period; // 'day', 'month', or 'year'
    private long newCustomers;
    private long returningCustomers;
    private Map<String, Long> customersByNationality;
    private Map<String, Long> customersByAgeGroup;
    private Map<String, Long> customersByGender;
    private String hotelId; // null for aggregate across all hotels
    private String hotelName;
}
