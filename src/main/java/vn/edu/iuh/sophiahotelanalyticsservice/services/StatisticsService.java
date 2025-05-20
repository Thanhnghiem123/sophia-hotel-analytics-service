package vn.edu.iuh.sophiahotelanalyticsservice.services;

import vn.edu.iuh.sophiahotelanalyticsservice.dtos.requests.DateRangeRequest;
import vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses.BookingStatisticsResponse;
import vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses.CustomerStatisticsResponse;
import vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses.OccupancyStatisticsResponse;
import vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses.OverviewStatisticsResponse;
import vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses.RevenueStatisticsResponse;

import java.util.List;

public interface StatisticsService {
    OverviewStatisticsResponse getOverviewStatistics();
    
    List<RevenueStatisticsResponse> getRevenueStatistics(DateRangeRequest request);
    
    List<OccupancyStatisticsResponse> getOccupancyStatistics(DateRangeRequest request);
    
    List<BookingStatisticsResponse> getBookingStatistics(DateRangeRequest request);
    
    List<CustomerStatisticsResponse> getCustomerStatistics(DateRangeRequest request);
    
    OverviewStatisticsResponse getHotelOverviewStatistics(String hotelId);
}
