package vn.edu.iuh.sophiahotelanalyticsservice.services;

import vn.edu.iuh.sophiahotelanalyticsservice.dtos.requests.DateRangeRequest;
import vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses.BookingStatisticsResponse;
import vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses.CustomerStatisticsResponse;
import vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses.OccupancyStatisticsResponse;
import vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses.OverviewStatisticsResponse;
import vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses.RevenueStatisticsResponse;
import vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses.HotelStatisticsResponse;
import vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses.UserStatisticsResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StatisticsService {
    OverviewStatisticsResponse getOverviewStatistics();

    
    // New methods for direct API calls to services
    Map<String, Object> getDirectRevenueStatistics(LocalDate from, LocalDate to);
    
    OccupancyStatisticsResponse getDirectOccupancyStatistics(LocalDate from, LocalDate to, String hotelId);
    
    Map<String, Object> getDirectBookingStatistics(LocalDate from, LocalDate to);
    
    UserStatisticsResponse getDirectCustomerStatistics(LocalDate from, LocalDate to);
    
    HotelStatisticsResponse getDirectHotelStatistics(String hotelId);

    int countActiveHotels();
}
