package vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatisticsResponse {
    private long totalUsers;
    private long newUsers;
    private long returningUsers;
    private Map<String, Long> usersByGender;
    private Map<String, Long> usersByAgeGroup;
}
