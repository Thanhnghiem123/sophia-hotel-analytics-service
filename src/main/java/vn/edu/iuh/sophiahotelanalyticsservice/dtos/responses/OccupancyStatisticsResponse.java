package vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OccupancyStatisticsResponse {
    private LocalDate date;
    private String period; // 'day', 'month', or 'year'
    private double occupancyRate; // 0.0 to 1.0
    private String hotelId; // null for aggregate across all hotels
    private String hotelName;
    private int totalRooms;
    private int occupiedRooms;
}
