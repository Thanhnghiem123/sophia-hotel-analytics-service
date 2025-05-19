package vn.edu.iuh.sophiahotelanalyticsservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.sophiahotelanalyticsservice.models.Report;

import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {
}
