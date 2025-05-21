package vn.edu.iuh.sophiahotelanalyticsservice.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest; // Thêm import này
import vn.edu.iuh.sophiahotelanalyticsservice.clients.dto.BookingResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BookingServiceClient {

    @Value("${service.booking}")
    private String bookingServiceUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request; // Inject HttpServletRequest

    public long getTotalTransactions() {
        try {
            String url = String.format("%s/api/v1/transactions/count", bookingServiceUrl);

            // Tạo HttpHeaders và thêm Bearer Token
            HttpHeaders headers = new HttpHeaders();
            String token = getAccessToken(); // Lấy token từ header Authorization
            if (token == null || token.isEmpty()) {
                System.out.println("Access token is null or empty");
                throw new RuntimeException("Failed to fetch transactions: No valid token available");
            }
            headers.set("Authorization", "Bearer " + token);

            // Tạo HttpEntity với headers
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Gửi yêu cầu với headers
            ResponseEntity<Long> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Long.class
            );
            return response.getBody() != null ? response.getBody() : 0L;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                System.out.println("Unauthorized: Invalid or expired token.");
                throw new RuntimeException("Failed to fetch transactions: Invalid or expired token");
            }
            System.out.println("Error fetching total transactions: " + e.getMessage());
            throw new RuntimeException("Failed to fetch transactions: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error fetching total transactions: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch transactions: " + e.getMessage());
        }
    }

    private String getAccessToken() {
        String header = request.getHeader("Authorization"); // Lấy header Authorization từ yêu cầu HTTP
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7); // Bỏ "Bearer " để lấy token
        }
        System.out.println("No authentication token found in Authorization header");
        return null;
    }

    // Các phương thức khác giữ nguyên, nhưng cũng cần thêm token nếu gọi API yêu cầu xác thực
    public List<BookingResponse> getBookingsByDateRange(LocalDate from, LocalDate to) {
        try {
            String url = String.format("%s/api/v1/bookings?from=%s&to=%s",
                    bookingServiceUrl,
                    from.format(DateTimeFormatter.ISO_DATE),
                    to.format(DateTimeFormatter.ISO_DATE));

            HttpHeaders headers = new HttpHeaders();
            String token = getAccessToken();
            if (token == null || token.isEmpty()) {
                System.out.println("Access token is null or empty");
                throw new RuntimeException("Failed to fetch bookings: No valid token available");
            }
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            System.out.println("Raw response from booking service: " + response.getBody());

            BookingResponse[] bookings = objectMapper.readValue(response.getBody(), BookingResponse[].class);
            return Arrays.asList(bookings);
        } catch (Exception e) {
            System.out.println("Error fetching bookings from booking service: " + e.getMessage());
            e.printStackTrace();
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

            HttpHeaders headers = new HttpHeaders();
            String token = getAccessToken();
            if (token == null || token.isEmpty()) {
                System.out.println("Access token is null or empty");
                throw new RuntimeException("Failed to fetch bookings: No valid token available");
            }
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            System.out.println("Raw response from booking service for hotel " + hotelId + ": " + response.getBody());

            BookingResponse[] bookings = objectMapper.readValue(response.getBody(), BookingResponse[].class);
            return Arrays.asList(bookings);
        } catch (Exception e) {
            System.out.println("Error fetching bookings for hotel " + hotelId + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch bookings for hotel: " + e.getMessage());
        }
    }

    public Map<String, Object> getRevenueStatistics(LocalDate from, LocalDate to) {
        try {
            String url = String.format("%s/api/v1/transactions/statistics/revenue?from=%s&to=%s",
                    bookingServiceUrl,
                    from.format(DateTimeFormatter.ISO_DATE),
                    to.format(DateTimeFormatter.ISO_DATE));

            HttpHeaders headers = new HttpHeaders();
            String token = getAccessToken();
            if (token == null || token.isEmpty()) {
                System.out.println("Access token is null or empty");
                throw new RuntimeException("Failed to fetch revenue statistics: No valid token available");
            }
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            System.out.println("Raw response from booking service revenue statistics: " + response.getBody());

            return objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            System.out.println("Error fetching revenue statistics: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch revenue statistics: " + e.getMessage());
        }
    }

    public Map<String, Object> getBookingStatistics(LocalDate from, LocalDate to) {
        try {
            String url = String.format("%s/api/v1/transactions/statistics/bookings?from=%s&to=%s",
                    bookingServiceUrl,
                    from.format(DateTimeFormatter.ISO_DATE),
                    to.format(DateTimeFormatter.ISO_DATE));

            HttpHeaders headers = new HttpHeaders();
            String token = getAccessToken();
            if (token == null || token.isEmpty()) {
                System.out.println("Access token is null or empty");
                throw new RuntimeException("Failed to fetch booking statistics: No valid token available");
            }
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            System.out.println("Raw response from booking service booking statistics: " + response.getBody());

            return objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            System.out.println("Error fetching booking statistics: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch booking statistics: " + e.getMessage());
        }
    }
}