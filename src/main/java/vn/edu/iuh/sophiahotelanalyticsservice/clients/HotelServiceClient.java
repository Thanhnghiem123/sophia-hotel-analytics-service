package vn.edu.iuh.sophiahotelanalyticsservice.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import vn.edu.iuh.sophiahotelanalyticsservice.clients.dto.HotelResponse;

import java.util.Collections;
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
        String url = hotelServiceUrl + "/api/v1/hotels";
        System.out.println("Hotel service URL: " + url);
        
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    String.class
            );
            
            if (response.getBody() == null) {
                log.warn("Received null response body from hotel service");
                return Collections.emptyList();
            }
            
            System.out.println("Raw response from hotel service: " + response.getBody());
            
            // Convert JSON array to List of HotelResponse
            return objectMapper.readValue(
                    response.getBody(),
                    new TypeReference<List<HotelResponse>>() {}
            );
        } catch (RestClientException e) {
            log.error("Error calling hotel service at {}: {}", url, e.getMessage());
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Error processing hotel service response: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
    
    
    public HotelResponse getHotelById(String hotelId) {
        String url = hotelServiceUrl + "/api/v1/hotels/" + hotelId;
        log.info("Fetching hotel by id: {}", hotelId);
        
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    String.class
            );
            
            log.debug("Raw response from hotel service: {}", response.getBody());
            
            if (response.getBody() == null) {
                log.warn("Received null response body for hotel id: {}", hotelId);
                return null;
            }
            
            return objectMapper.readValue(response.getBody(), HotelResponse.class);
        } catch (RestClientException e) {
            log.error("Error calling hotel service for id {}: {}", hotelId, e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Error processing hotel service response for id {}: {}", hotelId, e.getMessage());
            return null;
        }
    }

    public int getRoomCountByHotelId(String hotelId) {
        String url = hotelServiceUrl + "/api/v1/hotels/" + hotelId + "/room-count";
        System.out.println("Fetching room count from URL: " + url);
        
        try {
            ResponseEntity<Integer> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    Integer.class
            );

            int count = response.getBody() != null ? response.getBody() : 0;
            log.debug("Room count for hotel {}: {}", hotelId, count);
            
            return count;
        } catch (Exception e) {
            log.error("Error fetching room count for hotel {}: {}", hotelId, e.getMessage());
            return 0; // Return 0 as default if there's an error
        }
    }

    public int getAvailableRoomCount() {
        String url = hotelServiceUrl + "/api/v1/rooms/available/count";
        log.info("Fetching available room count");

        try {
            ResponseEntity<Integer> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    Integer.class
            );

            int availableRooms = response.getBody() != null ? response.getBody() : 0;
            log.debug("Available room count: {}", availableRooms);

            return availableRooms;
        } catch (Exception e) {
            log.error("Error fetching available room count: {}", e.getMessage());
            return 0; // Return 0 as default if there's an error
        }
    }

}
