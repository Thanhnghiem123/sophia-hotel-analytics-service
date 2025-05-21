package vn.edu.iuh.sophiahotelanalyticsservice.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.sophiahotelanalyticsservice.dtos.requests.DateRangeRequest;
import vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses.BookingStatisticsResponse;
import vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses.CustomerStatisticsResponse;
import vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses.OccupancyStatisticsResponse;
import vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses.OverviewStatisticsResponse;
import vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses.RevenueStatisticsResponse;
import vn.edu.iuh.sophiahotelanalyticsservice.services.StatisticsService;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/overview")
    public ResponseEntity<OverviewStatisticsResponse> getOverview() {
        return ResponseEntity.ok(statisticsService.getOverviewStatistics());
    }

    @GetMapping("/revenue")
    public ResponseEntity<List<RevenueStatisticsResponse>> getRevenueStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String groupBy,
            @RequestParam(required = false) String hotelId) {
        
        DateRangeRequest request = DateRangeRequest.builder()
                .from(from)
                .to(to)
                .groupBy(groupBy != null ? groupBy : "day")
                .hotelId(hotelId)
                .build();
                
        return ResponseEntity.ok(statisticsService.getRevenueStatistics(request));
    }

    @GetMapping("/occupancy")
    public ResponseEntity<List<OccupancyStatisticsResponse>> getOccupancyStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String groupBy,
            @RequestParam(required = false) String hotelId) {
        
        DateRangeRequest request = DateRangeRequest.builder()
                .from(from)
                .to(to)
                .groupBy(groupBy != null ? groupBy : "day")
                .hotelId(hotelId)
                .build();
                
        return ResponseEntity.ok(statisticsService.getOccupancyStatistics(request));
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingStatisticsResponse>> getBookingStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String groupBy,
            @RequestParam(required = false) String hotelId) {
        
        DateRangeRequest request = DateRangeRequest.builder()
                .from(from)
                .to(to)
                .groupBy(groupBy != null ? groupBy : "day")
                .hotelId(hotelId)
                .build();
                
        return ResponseEntity.ok(statisticsService.getBookingStatistics(request));
    }

    @GetMapping("/customers")
    public ResponseEntity<List<CustomerStatisticsResponse>> getCustomerStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String groupBy,
            @RequestParam(required = false) String hotelId) {
        
        DateRangeRequest request = DateRangeRequest.builder()
                .from(from)
                .to(to)
                .groupBy(groupBy != null ? groupBy : "day")
                .hotelId(hotelId)
                .build();
                
        return ResponseEntity.ok(statisticsService.getCustomerStatistics(request));
    }

    @GetMapping("/hotel/{hotelId}/overview")
    public ResponseEntity<OverviewStatisticsResponse> getHotelOverview(@PathVariable String hotelId) {
        return ResponseEntity.ok(statisticsService.getHotelOverviewStatistics(hotelId));
    }
}
