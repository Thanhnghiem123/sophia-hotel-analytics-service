package vn.edu.iuh.sophiahotelanalyticsservice.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses.UserStatisticsResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceClient {

    @Value("${service.user}")
    private String userServiceUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request; // Inject HttpServletRequest

    public int getUserCount() {
        String url = userServiceUrl + "/api/v1/users/count";
        log.info("Fetching user count");

        try {
            // Create headers and add Bearer Token
            HttpHeaders headers = new HttpHeaders();
            String token = getAccessToken();
            if (token == null || token.isEmpty()) {
                log.error("Access token is null or empty");
                throw new RuntimeException("Failed to fetch user count: No valid token available");
            }
            headers.set("Authorization", "Bearer " + token);

            // Create HttpEntity with headers
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Send request with headers
            ResponseEntity<Integer> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Integer.class
            );

            int userCount = response.getBody() != null ? response.getBody() : 0;
            log.debug("Total user count: {}", userCount);

            return userCount;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                log.error("Unauthorized: Invalid or expired token.");
                throw new RuntimeException("Failed to fetch user count: Invalid or expired token");
            }
            log.error("Error fetching user count: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch user count: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error fetching user count: {}", e.getMessage(), e);
            return 0;
        }
    }

    public UserStatisticsResponse getUserStatistics(LocalDate from, LocalDate to) {
        System.out.println("Attempting to get user statistics from: " + from + " to: " + to);
        String url = userServiceUrl + "/api/v1/users/statistics?from=" + from.format(DateTimeFormatter.ISO_DATE) +
                     "&to=" + to.format(DateTimeFormatter.ISO_DATE);
        log.info("Fetching user statistics from URL: {}", url);

        try {
            // Create headers and add Bearer Token
            HttpHeaders headers = new HttpHeaders();
            String token = getAccessToken();
            if (token == null || token.isEmpty()) {
                log.error("Access token is null or empty");
                throw new RuntimeException("Failed to fetch user statistics: No valid token available");
            }
            headers.set("Authorization", "Bearer " + token);

            // Create HttpEntity with headers
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Send request with headers
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            log.debug("Raw response from user service: {}", response.getBody());
    System.out.println("Token used: " + (token != null ? "Valid" : "Invalid"));
    System.out.println("URL called: " + url);
            if (response.getBody() == null) {
                log.warn("Received null response body for user statistics");
                return null;
            }

            return objectMapper.readValue(response.getBody(), UserStatisticsResponse.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                log.error("Unauthorized: Invalid or expired token.");
                throw new RuntimeException("Failed to fetch user statistics: Invalid or expired token");
            }
            log.error("Error fetching user statistics: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch user statistics: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error fetching user statistics: {}", e.getMessage(), e);
            return null;
        }
    }

    private String getAccessToken() {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7); // Remove "Bearer " to get the token
        }
        log.warn("No authentication token found in Authorization header");
        return null;
    }
}