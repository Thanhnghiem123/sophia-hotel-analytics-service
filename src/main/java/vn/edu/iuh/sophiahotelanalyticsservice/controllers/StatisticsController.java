package vn.edu.iuh.sophiahotelanalyticsservice.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.sophiahotelanalyticsservice.dtos.requests.DateRangeRequest;
import vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses.*;
import vn.edu.iuh.sophiahotelanalyticsservice.services.StatisticsService;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/direct/overview")
    public ResponseEntity<OverviewStatisticsResponse> getOverview() {
        return ResponseEntity.ok(statisticsService.getOverviewStatistics());
    }



    /**
     * b. Thống kê doanh thu (Revenue Statistics) - Direct call to Booking Service
     * API: GET /api/statistics/direct/revenue?from=YYYY-MM-DD&to=YYYY-MM-DD
     */
    @GetMapping("/direct/revenue")
    public ResponseEntity<?> getDirectRevenueStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        
        return ResponseEntity.ok(statisticsService.getDirectRevenueStatistics(from, to));
    }

    /**
     * c. Thống kê công suất phòng (Occupancy Rate) - Direct call to Hotel Service
     * API: GET /api/statistics/direct/occupancy?from=YYYY-MM-DD&to=YYYY-MM-DD
     */
    @GetMapping("/direct/occupancy")
    public ResponseEntity<OccupancyStatisticsResponse> getDirectOccupancyStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String hotelId) {
        
        return ResponseEntity.ok(statisticsService.getDirectOccupancyStatistics(from, to, hotelId));
    }

    /**
     * d. Thống kê booking (Booking Statistics) - Direct call to Booking Service
     * API: GET /api/statistics/direct/bookings?from=YYYY-MM-DD&to=YYYY-MM-DD
     */
    @GetMapping("/direct/bookings")
    public ResponseEntity<?> getDirectBookingStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        
        return ResponseEntity.ok(statisticsService.getDirectBookingStatistics(from, to));
    }

    /**
     * e. Thống kê khách hàng (Customer Statistics) - Direct call to User Service
     * API: GET /api/statistics/direct/customers?from=YYYY-MM-DD&to=YYYY-MM-DD
     */
    @GetMapping("/direct/customers")
    public ResponseEntity<UserStatisticsResponse> getDirectCustomerStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        
        return ResponseEntity.ok(statisticsService.getDirectCustomerStatistics(from, to));
    }

    /**
     * g. Thống kê theo từng khách sạn (Per-hotel Statistics) - Direct call to Hotel Service
     * API: GET /api/statistics/direct/hotel/{hotelId}
     */
    @GetMapping("/direct/hotel/{hotelId}/overview")
    public ResponseEntity<HotelStatisticsResponse> getDirectHotelStatistics(
            @PathVariable String hotelId) {
        
        return ResponseEntity.ok(statisticsService.getDirectHotelStatistics(hotelId));
    }
}
