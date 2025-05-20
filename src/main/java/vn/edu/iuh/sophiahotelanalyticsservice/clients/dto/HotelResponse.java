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
public class HotelResponse {
    private String id;
    private String name;
    private String address;
    private String description;
    private int starRating;
    private String contactEmail;
    private String contactPhone;
    private String imageUrl;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private boolean isActive;
}
