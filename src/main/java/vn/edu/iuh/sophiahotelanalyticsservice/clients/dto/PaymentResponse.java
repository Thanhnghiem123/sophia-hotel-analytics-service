package vn.edu.iuh.sophiahotelanalyticsservice.clients.dto;

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
public class PaymentResponse {
    private String id;
    private String bookingId;
    private BigDecimal amount;
    private String paymentMethod;
    private String status; // PENDING, COMPLETED, FAILED, REFUNDED
    private String transactionId;
    private LocalDateTime paymentDate;
    private LocalDateTime createdAt;
    private String notes;
}
