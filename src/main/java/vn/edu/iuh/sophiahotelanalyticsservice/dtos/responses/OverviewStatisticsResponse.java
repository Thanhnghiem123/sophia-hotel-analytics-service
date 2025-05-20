package vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OverviewStatisticsResponse {
    private long totalHotels;
    private long totalRooms;
    private long totalBookings;
    private BigDecimal totalRevenue;
    private long currentGuests;
    private long availableRooms;
}
