package com.santa4you.santa_delivery_service.service;

import com.santa4you.santa_delivery_service.model.SantaLocation;
import com.santa4you.santa_delivery_service.model.SantaStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SantaTrackingServiceTest {

    private SantaTrackingService santaTrackingService;

    @BeforeEach
    void setUp() {
        santaTrackingService = new SantaTrackingService();
    }


    @Test
    void getCurrentLocation_OnChristmasEve_ShouldReturnLocationBasedOnHour() {
        SantaLocation location = santaTrackingService.getCurrentLocation();
        
        assertThat(location).isNotNull();
        assertThat(location.getCity()).isNotNull();
        assertThat(location.getCountry()).isNotNull();
    }

    @Test
    void getCurrentLocation_ShouldHaveValidCoordinates() {
        SantaLocation location = santaTrackingService.getCurrentLocation();
        
        assertThat(location.getLatitude()).isBetween(-90.0, 90.0);
        assertThat(location.getLongitude()).isBetween(-180.0, 180.0);
    }

    @Test
    void getEstimatedArrivalTime_ForMumbai_ShouldReturnFixedTime() {
        String eta = santaTrackingService.getEstimatedArrivalTime("Mumbai");
        
        assertThat(eta).isEqualTo("December 25, 3:00 AM");
    }

    @Test
    void getEstimatedArrivalTime_ForNewYork_ShouldReturnFixedTime() {
        String eta = santaTrackingService.getEstimatedArrivalTime("New York");
        
        assertThat(eta).isEqualTo("December 25, 12:00 PM");
    }

    @Test
    void getEstimatedArrivalTime_CaseInsensitive_ShouldWork() {
        String etaLower = santaTrackingService.getEstimatedArrivalTime("tokyo");
        String etaUpper = santaTrackingService.getEstimatedArrivalTime("TOKYO");
        
        assertThat(etaLower).isEqualTo("December 24, 9:00 PM");
        assertThat(etaUpper).isEqualTo("December 24, 9:00 PM");
    }

    @Test
    void getEstimatedArrivalTime_ForUnknownCity_ShouldReturnGenericTime() {
        String eta = santaTrackingService.getEstimatedArrivalTime("Unknown City");
        
        assertThat(eta).isEqualTo("December 24, around midnight");
    }

    @Test
    void getEstimatedArrivalTime_WithNullCity_ShouldReturnGenericTime() {
        String eta = santaTrackingService.getEstimatedArrivalTime(null);
        
        assertThat(eta).isEqualTo("December 24, around midnight");
    }

    @Test
    void getNextLocation_ShouldReturnValidLocation() {
        SantaLocation nextLocation = santaTrackingService.getNextLocation();
        
        assertThat(nextLocation).isNotNull();
        assertThat(nextLocation.getCity()).isNotNull();
    }
}