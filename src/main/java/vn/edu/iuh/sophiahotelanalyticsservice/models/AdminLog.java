package vn.edu.iuh.sophiahotelanalyticsservice.models;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "admin_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminLog {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "admin_id", columnDefinition = "uuid", nullable = false)
    private UUID adminId;

    @Column(columnDefinition = "text", nullable = false)
    private String action;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = new Timestamp(System.currentTimeMillis());
    }
}
