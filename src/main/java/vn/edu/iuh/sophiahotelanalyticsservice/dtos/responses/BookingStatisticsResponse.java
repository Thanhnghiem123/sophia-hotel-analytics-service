package vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingStatisticsResponse {
    private LocalDate date;
    private String period; // 'day', 'month', or 'year'
    private long newBookings;
    private long cancelledBookings;
    private long completedBookings;
    private String hotelId; // null for aggregate across all hotels
    private String hotelName;
}
