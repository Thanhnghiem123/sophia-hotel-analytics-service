package vn.edu.iuh.sophiahotelanalyticsservice.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.iuh.sophiahotelanalyticsservice.clients.BookingServiceClient;
import vn.edu.iuh.sophiahotelanalyticsservice.clients.HotelServiceClient;
import vn.edu.iuh.sophiahotelanalyticsservice.clients.PaymentServiceClient;
import vn.edu.iuh.sophiahotelanalyticsservice.clients.UserServiceClient;
import vn.edu.iuh.sophiahotelanalyticsservice.clients.dto.BookingResponse;
import vn.edu.iuh.sophiahotelanalyticsservice.clients.dto.HotelResponse;
import vn.edu.iuh.sophiahotelanalyticsservice.clients.dto.PaymentResponse;
import vn.edu.iuh.sophiahotelanalyticsservice.dtos.requests.DateRangeRequest;
import vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses.*;
import vn.edu.iuh.sophiahotelanalyticsservice.services.StatisticsService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final HotelServiceClient hotelServiceClient;
    private final BookingServiceClient bookingServiceClient;
    private final PaymentServiceClient paymentServiceClient;
    private final UserServiceClient userServiceClient;
    private final ObjectMapper objectMapper;

    @Override
    public OverviewStatisticsResponse getOverviewStatistics() {
        log.info("Fetching overview statistics...");
        try {
            // Lấy danh sách khách sạn
            List<HotelResponse> hotels = Collections.emptyList();
            int totalHotels = 0;
            int totalRooms = 0;

            try {
                hotels = hotelServiceClient.getAllHotels();
                totalHotels = countActiveHotels();

                System.out.println("Hotels: " + hotels);
                System.out.println("Total hotels: " + totalHotels);

                // Tính tổng số phòng
                for (HotelResponse hotel : hotels) {
                    try {
                        int roomCount = hotelServiceClient.getRoomCountByHotelId(hotel.getId());
                        totalRooms += roomCount;
                        log.debug("Hotel {} has {} rooms", hotel.getId(), roomCount);
                    } catch (Exception e) {
                        log.error("Error fetching rooms for hotel {}: {}", hotel.getId(), e.getMessage());
                    }
                }
            } catch (Exception e) {
                log.error("Error fetching hotels: {}", e.getMessage());
            }

            System.out.println("Total rooms: " + totalRooms);

            // Get total transactions (bookings)
            long totalBookings = 0;
            try {
                totalBookings = bookingServiceClient.getTotalTransactions();
                log.info("Fetched total transactions: {}", totalBookings);
            } catch (Exception e) {
                log.error("Error fetching total transactions: {}", e.getMessage());
            }

            BigDecimal totalRevenue = BigDecimal.ZERO;
            try {
                LocalDate today = LocalDate.now();
                List<PaymentResponse> allPayments = paymentServiceClient.getPaymentsByDateRange(
                        today.minusMonths(6), // Last 6 months
                        today
                );

                if (allPayments != null && !allPayments.isEmpty()) {
                    totalRevenue = allPayments.stream()
                            .map(PaymentResponse::getAmount)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    // Fix: assign the result of setScale back to totalRevenue
                    totalRevenue = totalRevenue.setScale(0, RoundingMode.HALF_UP);
                }
            } catch (Exception e) {
                log.error("Error calculating total revenue: {}", e.getMessage());
            }

//            getAvailableRoomCount
            int availableRooms = hotelServiceClient.getAvailableRoomCount();

//                    getUserCount
            int userCount = userServiceClient.getUserCount();

            return OverviewStatisticsResponse.builder()
                    .totalHotels(totalHotels)
                    .totalRooms(totalRooms)
                    .totalBookings(totalBookings)
                    .totalRevenue(totalRevenue)
                    .currentGuests(userCount) // TODO: Implement this if needed
                    .availableRooms(availableRooms)  // TODO: Implement this if needed
                    .build();

        } catch (Exception e) {
            log.error("Error fetching overview statistics", e);
            throw new RuntimeException("Failed to fetch overview statistics: " + e.getMessage(), e);
        }
    }


    @Override
    public Map<String, Object> getDirectRevenueStatistics(LocalDate from, LocalDate to) {
        log.info("Fetching direct revenue statistics from booking service for period {} to {}", from, to);
        try {
            return bookingServiceClient.getRevenueStatistics(from, to);
        } catch (Exception e) {
            log.error("Error fetching direct revenue statistics", e);
            throw new RuntimeException("Failed to fetch revenue statistics: " + e.getMessage(), e);
        }
    }

    @Override
    public OccupancyStatisticsResponse getDirectOccupancyStatistics(LocalDate from, LocalDate to, String hotelId) {
        log.info("Fetching direct occupancy statistics for period {} to {}, hotelId: {}", from, to, hotelId);
        try {
            return hotelServiceClient.getOccupancyStatistics(from, to, hotelId);
        } catch (Exception e) {
            log.error("Error fetching direct occupancy statistics", e);
            throw new RuntimeException("Failed to fetch occupancy statistics: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> getDirectBookingStatistics(LocalDate from, LocalDate to) {
        log.info("Fetching direct booking statistics from booking service for period {} to {}", from, to);
        try {
            return bookingServiceClient.getBookingStatistics(from, to);
        } catch (Exception e) {
            log.error("Error fetching direct booking statistics", e);
            throw new RuntimeException("Failed to fetch booking statistics: " + e.getMessage(), e);
        }
    }

    @Override
    public UserStatisticsResponse getDirectCustomerStatistics(LocalDate from, LocalDate to) {
        log.info("Fetching direct customer statistics from user service for period {} to {}", from, to);
        try {
            return userServiceClient.getUserStatistics(from, to);
        } catch (Exception e) {
            log.error("Error fetching direct customer statistics", e);
            throw new RuntimeException("Failed to fetch customer statistics: " + e.getMessage(), e);
        }
    }

    @Override
    public HotelStatisticsResponse getDirectHotelStatistics(String hotelId) {
        log.info("Fetching direct hotel statistics for hotelId: {}", hotelId);
        try {
            return hotelServiceClient.getHotelStatisticsOverview(hotelId);
        } catch (Exception e) {
            log.error("Error fetching direct hotel statistics", e);
            throw new RuntimeException("Failed to fetch hotel statistics: " + e.getMessage(), e);
        }
    }

    @Override
    public int countActiveHotels() {
        log.info("Fetching active hotels count");
        try {
            return (int) hotelServiceClient.countActiveHotels();
        } catch (Exception e) {
            log.error("Error fetching active hotels count", e);
            throw new RuntimeException("Failed to fetch active hotels count: " + e.getMessage(), e);
        }
    }
}
