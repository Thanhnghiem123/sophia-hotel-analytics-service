package vn.edu.iuh.sophiahotelanalyticsservice.clients.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    private String id;
    private String roomNumber;
    private String roomTypeId;
    private String hotelId;
    private boolean isAvailable;
    private BigDecimal pricePerNight;
    private String description;
    private int capacity;
    private String[] amenities;
    private String status;
}
