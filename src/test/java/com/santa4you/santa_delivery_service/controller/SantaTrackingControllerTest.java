package com.santa4you.santa_delivery_service.controller;

import com.santa4you.santa_delivery_service.model.SantaLocation;
import com.santa4you.santa_delivery_service.model.SantaStatus;
import com.santa4you.santa_delivery_service.service.SantaTrackingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SantaTrackingController.class)
class SantaTrackingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SantaTrackingService santaTrackingService;

    @Test
    void getCurrentLocation_ShouldReturnOk() throws Exception {
        SantaLocation location = new SantaLocation(
            "Tokyo", "Japan", 35.6762, 139.6503,
            SantaStatus.DELIVERING_PRESENTS, "December 24, 9:00 PM"
        );
        when(santaTrackingService.getCurrentLocation()).thenReturn(location);

        mockMvc.perform(get("/api/v1/santa/location"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.city").value("Tokyo"))
            .andExpect(jsonPath("$.country").value("Japan"))
            .andExpect(jsonPath("$.latitude").value(35.6762))
            .andExpect(jsonPath("$.longitude").value(139.6503))
            .andExpect(jsonPath("$.status").value("DELIVERING_PRESENTS"))
            .andExpect(jsonPath("$.estimatedArrival").value("December 24, 9:00 PM"));
    }

    @Test
    void getCurrentLocation_WhenAtNorthPole_ShouldReturnCorrectStatus() throws Exception {
        SantaLocation location = new SantaLocation(
            "North Pole Workshop", "Arctic", 90.0, 0.0,
            SantaStatus.PREPARING, "December 24, 6:00 PM"
        );
        when(santaTrackingService.getCurrentLocation()).thenReturn(location);

        mockMvc.perform(get("/api/v1/santa/location"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PREPARING"));
    }

    @Test
    void getEstimatedArrival_WithValidCity_ShouldReturnEta() throws Exception {
        when(santaTrackingService.getEstimatedArrivalTime("Mumbai"))
            .thenReturn("December 25, 3:00 AM");

        mockMvc.perform(get("/api/v1/santa/eta").param("city", "Mumbai"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.city").value("Mumbai"))
            .andExpect(jsonPath("$.estimatedArrival").value("December 25, 3:00 AM"))
            .andExpect(jsonPath("$.message").value("Santa will arrive at Mumbai around December 25, 3:00 AM"));
    }

    @Test
    void getEstimatedArrival_WithUnknownCity_ShouldReturnGenericEta() throws Exception {
        when(santaTrackingService.getEstimatedArrivalTime("Paris"))
            .thenReturn("December 24, around midnight");

        mockMvc.perform(get("/api/v1/santa/eta").param("city", "Paris"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estimatedArrival").value("December 24, around midnight"));
    }

}
