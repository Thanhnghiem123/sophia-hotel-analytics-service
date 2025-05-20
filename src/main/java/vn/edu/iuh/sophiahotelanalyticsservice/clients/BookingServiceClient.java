package vn.edu.iuh.sophiahotelanalyticsservice.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import vn.edu.iuh.sophiahotelanalyticsservice.clients.dto.BookingResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingServiceClient {
    
    @Value("${service.booking}")
    private String bookingServiceUrl;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public List<BookingResponse> getBookingsByDateRange(LocalDate from, LocalDate to) {
        try {
            String url = String.format("%s/api/v1/bookings?from=%s&to=%s",
                    bookingServiceUrl,
                    from.format(DateTimeFormatter.ISO_DATE),
                    to.format(DateTimeFormatter.ISO_DATE));
            
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    String.class
            );
            
            log.debug("Raw response from booking service: {}", response.getBody());
            
            // Convert JSON array to List of BookingResponse
            BookingResponse[] bookings = objectMapper.readValue(response.getBody(), BookingResponse[].class);
            return Arrays.asList(bookings);
        } catch (Exception e) {
            log.error("Error fetching bookings from booking service", e);
            throw new RuntimeException("Failed to fetch bookings: " + e.getMessage());
        }
    }
    
    public List<BookingResponse> getBookingsByHotelAndDateRange(String hotelId, LocalDate from, LocalDate to) {
        try {
            String url = String.format("%s/api/v1/bookings/hotel/%s?from=%s&to=%s",
                    bookingServiceUrl,
                    hotelId,
                    from.format(DateTimeFormatter.ISO_DATE),
                    to.format(DateTimeFormatter.ISO_DATE));
            
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    String.class
            );
            
            log.debug("Raw response from booking service for hotel {}: {}", hotelId, response.getBody());
            
            // Convert JSON array to List of BookingResponse
            BookingResponse[] bookings = objectMapper.readValue(response.getBody(), BookingResponse[].class);
            return Arrays.asList(bookings);
        } catch (Exception e) {
            log.error("Error fetching bookings for hotel: " + hotelId, e);
            throw new RuntimeException("Failed to fetch bookings for hotel: " + e.getMessage());
        }
    }
}
