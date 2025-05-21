package vn.edu.iuh.sophiahotelanalyticsservice.clients;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceClient {

    @Value("${service.user}")
    private String userServiceUrl;

    private final RestTemplate restTemplate;
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

    private String getAccessToken() {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7); // Remove "Bearer " to get the token
        }
        log.warn("No authentication token found in Authorization header");
        return null;
    }
}