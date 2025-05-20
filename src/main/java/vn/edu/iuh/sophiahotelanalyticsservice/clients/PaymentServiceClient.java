package vn.edu.iuh.sophiahotelanalyticsservice.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import vn.edu.iuh.sophiahotelanalyticsservice.clients.dto.PaymentResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentServiceClient {
    
    @Value("${service.payment}")
    private String paymentServiceUrl;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public List<PaymentResponse> getPaymentsByDateRange(LocalDate from, LocalDate to) {
        try {
            String url = String.format("%s/api/v1/payments?from=%s&to=%s",
                    paymentServiceUrl,
                    from.format(DateTimeFormatter.ISO_DATE),
                    to.format(DateTimeFormatter.ISO_DATE));
            
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    String.class
            );
            
            log.debug("Raw response from payment service: {}", response.getBody());
            
            // Convert JSON array to List of PaymentResponse
            PaymentResponse[] payments = objectMapper.readValue(response.getBody(), PaymentResponse[].class);
            return Arrays.asList(payments);
        } catch (Exception e) {
            log.error("Error fetching payments from payment service", e);
            throw new RuntimeException("Failed to fetch payments: " + e.getMessage());
        }
    }
    
    public PaymentResponse getPaymentByBookingId(String bookingId) {
        try {
            String url = String.format("%s/api/v1/payments/booking/%s", paymentServiceUrl, bookingId);
            
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    String.class
            );
            
            log.debug("Raw response from payment service for booking {}: {}", bookingId, response.getBody());
            
            return objectMapper.readValue(response.getBody(), PaymentResponse.class);
        } catch (Exception e) {
            log.error("Error fetching payment for booking: " + bookingId, e);
            throw new RuntimeException("Failed to fetch payment: " + e.getMessage());
        }
    }
}
