package vn.edu.iuh.sophiahotelanalyticsservice.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.iuh.sophiahotelanalyticsservice.clients.BookingServiceClient;
import vn.edu.iuh.sophiahotelanalyticsservice.clients.HotelServiceClient;
import vn.edu.iuh.sophiahotelanalyticsservice.clients.PaymentServiceClient;
import vn.edu.iuh.sophiahotelanalyticsservice.clients.dto.BookingResponse;
import vn.edu.iuh.sophiahotelanalyticsservice.clients.dto.HotelResponse;
import vn.edu.iuh.sophiahotelanalyticsservice.clients.dto.PaymentResponse;
import vn.edu.iuh.sophiahotelanalyticsservice.dtos.requests.DateRangeRequest;
import vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses.BookingStatisticsResponse;
import vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses.CustomerStatisticsResponse;
import vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses.OccupancyStatisticsResponse;
import vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses.OverviewStatisticsResponse;
import vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses.RevenueStatisticsResponse;
import vn.edu.iuh.sophiahotelanalyticsservice.services.StatisticsService;

import java.math.BigDecimal;
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
                totalHotels = hotels.size();

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

//            BigDecimal totalRevenue = BigDecimal.ZERO;
//            List<PaymentResponse> allPayments = paymentServiceClient.getPaymentsByDateRange(
//                    today.minusMonths(6), // Last 6 months
//                    today
//            );
//
//            if (!allPayments.isEmpty()) {
//                totalRevenue = allPayments.stream()
//                        .map(PaymentResponse::getAmount)
//                        .reduce(BigDecimal.ZERO, BigDecimal::add);
//            }

            return OverviewStatisticsResponse.builder()
                    .totalHotels(totalHotels)
                    .totalRooms(totalRooms)
                    .totalBookings(0)
                    .totalRevenue(BigDecimal.ZERO)
                    .currentGuests(0)
                    .availableRooms(0)
                    .build();

        } catch (Exception e) {
            log.error("Error fetching overview statistics", e);
            throw new RuntimeException("Failed to fetch overview statistics: " + e.getMessage(), e);
        }
    }

    @Override
    public List<RevenueStatisticsResponse> getRevenueStatistics(DateRangeRequest request) {
        try {
            // Fetch all payments in the date range
            List<PaymentResponse> payments = paymentServiceClient.getPaymentsByDateRange(
                    request.getFrom() != null ? request.getFrom() : LocalDate.now().minusMonths(1),
                    request.getTo() != null ? request.getTo() : LocalDate.now()
            );

            // Filter by hotel if specified
            if (request.getHotelId() != null && !request.getHotelId().isEmpty()) {
                // Get all booking IDs for the hotel
                List<BookingResponse> hotelBookings = bookingServiceClient.getBookingsByHotelAndDateRange(
                        request.getHotelId(),
                        request.getFrom() != null ? request.getFrom() : LocalDate.now().minusMonths(1),
                        request.getTo() != null ? request.getTo() : LocalDate.now()
                );

                Set<String> hotelBookingIds = hotelBookings.stream()
                        .map(BookingResponse::getId)
                        .collect(Collectors.toSet());

                // Filter payments for this hotel's bookings
                payments = payments.stream()
                        .filter(payment -> hotelBookingIds.contains(payment.getBookingId()))
                        .collect(Collectors.toList());
            }

            // Group by period
            Map<LocalDate, BigDecimal> revenueByDate = payments.stream()
                    .collect(Collectors.groupingBy(
                            payment -> groupByDate(payment.getPaymentDate().toLocalDate(), request.getGroupBy()),
                            Collectors.reducing(
                                    BigDecimal.ZERO,
                                    PaymentResponse::getAmount,
                                    BigDecimal::add
                            )
                    ));

            // Convert to response objects
            return revenueByDate.entrySet().stream()
                    .map(entry -> RevenueStatisticsResponse.builder()
                            .date(entry.getKey())
                            .period(request.getGroupBy())
                            .revenue(entry.getValue())
                            .hotelId(request.getHotelId())
                            .hotelName(request.getHotelId() != null ?
                                    hotelServiceClient.getHotelById(request.getHotelId()).getName() : "All Hotels")
                            .build())
                    .sorted(Comparator.comparing(RevenueStatisticsResponse::getDate))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error fetching revenue statistics", e);
            throw new RuntimeException("Failed to fetch revenue statistics: " + e.getMessage(), e);
        }
    }

    private LocalDate groupByDate(LocalDate date, String period) {
        if (period == null || "day".equalsIgnoreCase(period)) {
            return date;
        } else if ("month".equalsIgnoreCase(period)) {
            return date.withDayOfMonth(1);
        } else if ("year".equalsIgnoreCase(period)) {
            return date.withDayOfYear(1);
        }
        return date;
    }

    @Override
    public List<OccupancyStatisticsResponse> getOccupancyStatistics(DateRangeRequest request) {
        try {
            // Default date range if not provided
            LocalDate from = request.getFrom() != null ? request.getFrom() : LocalDate.now().minusMonths(1);
            LocalDate to = request.getTo() != null ? request.getTo() : LocalDate.now();

            // Get all bookings for the date range
            List<BookingResponse> bookings = request.getHotelId() != null && !request.getHotelId().isEmpty()
                    ? bookingServiceClient.getBookingsByHotelAndDateRange(request.getHotelId(), from, to)
                    : bookingServiceClient.getBookingsByDateRange(from, to);

            // Group bookings by date and count occupied rooms
            Map<LocalDate, Long> occupiedRoomsByDate = bookings.stream()
                    .filter(booking -> "CONFIRMED".equals(booking.getStatus()) || "CHECKED_IN".equals(booking.getStatus()))
                    .collect(Collectors.groupingBy(
                            booking -> groupByDate(booking.getCheckInDate(), request.getGroupBy()),
                            Collectors.counting()
                    ));

            // Get total rooms (simplified - in a real app, fetch from room service)
            int totalRooms = 100; // This should be fetched from the room service

            // Convert to response objects
            return occupiedRoomsByDate.entrySet().stream()
                    .map(entry -> {
                        LocalDate date = entry.getKey();
                        long occupiedRooms = entry.getValue();
                        double occupancyRate = totalRooms > 0 ? (double) occupiedRooms / totalRooms : 0.0;

                        return OccupancyStatisticsResponse.builder()
                                .date(date)
                                .period(request.getGroupBy())
                                .occupancyRate(occupancyRate)
                                .hotelId(request.getHotelId())
                                .hotelName(request.getHotelId() != null ?
                                        hotelServiceClient.getHotelById(request.getHotelId()).getName() : "All Hotels")
                                .totalRooms(totalRooms)
                                .occupiedRooms((int) occupiedRooms)
                                .build();
                    })
                    .sorted(Comparator.comparing(OccupancyStatisticsResponse::getDate))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error fetching occupancy statistics", e);
            throw new RuntimeException("Failed to fetch occupancy statistics: " + e.getMessage(), e);
        }
    }

    @Override
    public List<BookingStatisticsResponse> getBookingStatistics(DateRangeRequest request) {
        try {
            // Default date range if not provided
            LocalDate from = request.getFrom() != null ? request.getFrom() : LocalDate.now().minusMonths(1);
            LocalDate to = request.getTo() != null ? request.getTo() : LocalDate.now();

            // Get all bookings for the date range
            List<BookingResponse> bookings = request.getHotelId() != null && !request.getHotelId().isEmpty()
                    ? bookingServiceClient.getBookingsByHotelAndDateRange(request.getHotelId(), from, to)
                    : bookingServiceClient.getBookingsByDateRange(from, to);

            // Group bookings by date and status
            Map<LocalDate, Map<String, Long>> bookingsByDateAndStatus = bookings.stream()
                    .collect(Collectors.groupingBy(
                            booking -> groupByDate(booking.getCreatedAt().toLocalDate(), request.getGroupBy()),
                            Collectors.groupingBy(
                                    BookingResponse::getStatus,
                                    Collectors.counting()
                            )
                    ));

            // Convert to response objects
            return bookingsByDateAndStatus.entrySet().stream()
                    .map(entry -> {
                        LocalDate date = entry.getKey();
                        Map<String, Long> statusCounts = entry.getValue();

                        return BookingStatisticsResponse.builder()
                                .date(date)
                                .period(request.getGroupBy())
                                .newBookings(statusCounts.getOrDefault("PENDING", 0L) +
                                           statusCounts.getOrDefault("CONFIRMED", 0L))
                                .cancelledBookings(statusCounts.getOrDefault("CANCELLED", 0L))
                                .completedBookings(statusCounts.getOrDefault("COMPLETED", 0L))
                                .hotelId(request.getHotelId())
                                .hotelName(request.getHotelId() != null ?
                                        hotelServiceClient.getHotelById(request.getHotelId()).getName() : "All Hotels")
                                .build();
                    })
                    .sorted(Comparator.comparing(BookingStatisticsResponse::getDate))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error fetching booking statistics", e);
            throw new RuntimeException("Failed to fetch booking statistics: " + e.getMessage(), e);
        }
    }

    @Override
    public List<CustomerStatisticsResponse> getCustomerStatistics(DateRangeRequest request) {
        try {
            // Default date range if not provided
            LocalDate from = request.getFrom() != null ? request.getFrom() : LocalDate.now().minusMonths(1);
            LocalDate to = request.getTo() != null ? request.getTo() : LocalDate.now();

            // Get all bookings for the date range
            List<BookingResponse> bookings = request.getHotelId() != null && !request.getHotelId().isEmpty()
                    ? bookingServiceClient.getBookingsByHotelAndDateRange(request.getHotelId(), from, to)
                    : bookingServiceClient.getBookingsByDateRange(from, to);

            // Get unique customers (simplified - in a real app, fetch from user service)
            Map<String, BookingResponse> uniqueCustomers = new HashMap<>();
            Map<String, Long> customerVisitCount = new HashMap<>();

            for (BookingResponse booking : bookings) {
                String customerEmail = booking.getGuestEmail();
                if (customerEmail != null && !customerEmail.isEmpty()) {
                    uniqueCustomers.putIfAbsent(customerEmail, booking);
                    customerVisitCount.put(customerEmail, customerVisitCount.getOrDefault(customerEmail, 0L) + 1);
                }
            }

            // Count new vs returning customers (simplified logic)
            long newCustomers = uniqueCustomers.entrySet().stream()
                    .filter(entry -> {
                        // In a real app, check if this is the first booking for this customer
                        // For now, we'll consider a customer as new if they have only one booking
                        return customerVisitCount.getOrDefault(entry.getKey(), 0L) == 1;
                    })
                    .count();

            long returningCustomers = uniqueCustomers.size() - newCustomers;

            // Group by nationality and gender (simplified - in a real app, fetch from user profiles)
            Map<String, Long> byNationality = uniqueCustomers.values().stream()
                    .collect(Collectors.groupingBy(
                            booking -> "Unknown", // In a real app, get from user profile
                            Collectors.counting()
                    ));

            Map<String, Long> byGender = uniqueCustomers.values().stream()
                    .collect(Collectors.groupingBy(
                            booking -> "UNKNOWN", // In a real app, get from user profile
                            Collectors.counting()
                    ));

            // Group by age (simplified - in a real app, calculate from date of birth)
            Map<String, Long> byAgeGroup = Map.of("18-30", (long) uniqueCustomers.size());

            // Create response
            return List.of(CustomerStatisticsResponse.builder()
                    .date(groupByDate(from, request.getGroupBy()))
                    .period(request.getGroupBy())
                    .newCustomers(newCustomers)
                    .returningCustomers(returningCustomers)
                    .customersByNationality(byNationality)
                    .customersByAgeGroup(byAgeGroup)
                    .customersByGender(byGender)
                    .hotelId(request.getHotelId())
                    .hotelName(request.getHotelId() != null ?
                            hotelServiceClient.getHotelById(request.getHotelId()).getName() : "All Hotels")
                    .build());

        } catch (Exception e) {
            log.error("Error fetching customer statistics", e);
            throw new RuntimeException("Failed to fetch customer statistics: " + e.getMessage(), e);
        }
    }

    @Override
    public OverviewStatisticsResponse getHotelOverviewStatistics(String hotelId) {
        try {
            // Get hotel details
            HotelResponse hotel = hotelServiceClient.getHotelById(hotelId);
            if (hotel == null) {
                throw new IllegalArgumentException("Hotel not found with id: " + hotelId);
            }

            LocalDate today = LocalDate.now();

            // Get today's bookings for this hotel
            List<BookingResponse> todaysBookings = bookingServiceClient.getBookingsByHotelAndDateRange(
                    hotelId,
                    today,
                    today.plusDays(1)
            );

            // Calculate current guests (simplified)
            int currentGuests = todaysBookings.stream()
                    .filter(booking -> "CHECKED_IN".equals(booking.getStatus()))
                    .mapToInt(BookingResponse::getNumberOfGuests)
                    .sum();

            // Get all time bookings for this hotel (for total bookings and revenue)
            List<BookingResponse> allTimeBookings = bookingServiceClient.getBookingsByHotelAndDateRange(
                    hotelId,
                    today.minusYears(1), // Last year
                    today.plusDays(1)
            );

            // Calculate total bookings
            int totalBookings = allTimeBookings.size();

            // Calculate total revenue (simplified - in a real app, fetch from payment service)
            BigDecimal totalRevenue = allTimeBookings.stream()
                    .map(booking -> {
                        try {
                            PaymentResponse payment = paymentServiceClient.getPaymentByBookingId(booking.getId());
                            return payment != null ? payment.getAmount() : BigDecimal.ZERO;
                        } catch (Exception e) {
                            log.warn("Error fetching payment for booking: " + booking.getId(), e);
                            return BigDecimal.ZERO;
                        }
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Get total rooms (simplified - in a real app, fetch from room service)
            int totalRooms = 50; // Default value

            // Calculate available rooms (simplified)
            int occupiedRooms = (int) todaysBookings.stream()
                    .filter(booking -> "CHECKED_IN".equals(booking.getStatus()) || "CONFIRMED".equals(booking.getStatus()))
                    .count();

            int availableRooms = Math.max(0, totalRooms - occupiedRooms);

            return OverviewStatisticsResponse.builder()
                    .totalHotels(1) // Just this hotel
                    .totalRooms(totalRooms)
                    .totalBookings(totalBookings)
                    .totalRevenue(totalRevenue)
                    .currentGuests(currentGuests)
                    .availableRooms(availableRooms)
                    .build();

        } catch (Exception e) {
            log.error("Error fetching hotel overview statistics for hotel: " + hotelId, e);
            throw new RuntimeException("Failed to fetch hotel overview statistics: " + e.getMessage(), e);
        }
    }
}
