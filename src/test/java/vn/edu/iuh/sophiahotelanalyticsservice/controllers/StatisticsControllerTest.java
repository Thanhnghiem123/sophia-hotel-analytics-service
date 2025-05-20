package vn.edu.iuh.sophiahotelanalyticsservice.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService statisticsService;

    @Test
    public void whenServiceUnavailable_thenFallbackResponse() throws Exception {
        // Simulate service failure
        when(statisticsService.getOverviewStats()).thenThrow(new RuntimeException("Service unavailable"));

        // Should return fallback response
        mockMvc.perform(get("/statistics/overview"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.totalHotels").value(0))
               .andExpect(jsonPath("$.totalRooms").value(0))
               .andExpect(jsonPath("$.totalBookings").value(0))
               .andExpect(jsonPath("$.totalRevenue").value(0))
               .andExpect(jsonPath("$.currentGuests").value(0))
               .andExpect(jsonPath("$.availableRooms").value(0));
    }

    @Test
    public void whenServiceAvailable_thenReturnStats() throws Exception {
        // Mock successful service response
        OverviewStatsResponse response = OverviewStatsResponse.builder()
            .totalHotels(5)
            .totalRooms(100)
            .totalBookings(50)
            .totalRevenue(10000.0)
            .currentGuests(25)
            .availableRooms(75)
            .build();

        when(statisticsService.getOverviewStats()).thenReturn(response);

        // Should return actual stats
        mockMvc.perform(get("/statistics/overview"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.totalHotels").value(5))
               .andExpect(jsonPath("$.totalRooms").value(100))
               .andExpect(jsonPath("$.totalBookings").value(50))
               .andExpect(jsonPath("$.totalRevenue").value(10000.0))
               .andExpect(jsonPath("$.currentGuests").value(25))
               .andExpect(jsonPath("$.availableRooms").value(75));
    }
}
