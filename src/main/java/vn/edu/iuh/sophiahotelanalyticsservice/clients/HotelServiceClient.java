package vn.edu.iuh.sophiahotelanalyticsservice.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import vn.edu.iuh.sophiahotelanalyticsservice.clients.dto.HotelResponse;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HotelServiceClient {
    
    @Value("${service.hotel}")
    private String hotelServiceUrl;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public List<HotelResponse> getAllHotels() {
        try {
            String url = hotelServiceUrl + "/api/v1/hotels";
            
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    String.class
            );
            
            log.debug("Raw response from hotel service: {}", response.getBody());
            
            // Convert JSON array to List of HotelResponse
            HotelResponse[] hotels = objectMapper.readValue(response.getBody(), HotelResponse[].class);
            return Arrays.asList(hotels);
        } catch (Exception e) {
            log.error("Error fetching hotels from hotel service", e);
            throw new RuntimeException("Failed to fetch hotels: " + e.getMessage());
        }
    }
    
    public HotelResponse getHotelById(String hotelId) {
        try {
            String url = hotelServiceUrl + "/api/v1/hotels/" + hotelId;
            
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    String.class
            );
            
            log.debug("Raw response from hotel service: {}", response.getBody());
            
            return objectMapper.readValue(response.getBody(), HotelResponse.class);
        } catch (Exception e) {
            log.error("Error fetching hotel with id: " + hotelId, e);
            throw new RuntimeException("Failed to fetch hotel: " + e.getMessage());
        }
    }
}
