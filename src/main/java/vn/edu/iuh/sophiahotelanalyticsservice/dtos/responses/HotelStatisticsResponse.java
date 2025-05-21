package vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelStatisticsResponse {
    private String hotelId;
    String hotelName;
    private  int totalRooms;
    private  int occupiedRooms;
    private  double occupancyRate;
    private  double averageRating;
    private int totalRatings;
    private  BigDecimal averageRoomPrice;
    private int availableRooms;
    private int reservedRooms;
    private int bookedRooms;
}
