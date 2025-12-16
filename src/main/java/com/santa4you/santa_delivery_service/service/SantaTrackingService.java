package com.santa4you.santa_delivery_service.service;

import com.santa4you.santa_delivery_service.model.SantaLocation;
import com.santa4you.santa_delivery_service.model.SantaStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class SantaTrackingService {

    private final List<SantaLocation> locations = Arrays.asList(
            new SantaLocation("North Pole", "Arctic", 90.0, 0.0,
                    SantaStatus.STARTING_JOURNEY, "December 24, 6:00 PM"),
            new SantaLocation("Tokyo", "Japan", 35.6762, 139.6503,
                    SantaStatus.DELIVERING_PRESENTS, "December 24, 9:00 PM"),
            new SantaLocation("Sydney", "Australia", -33.8688, 151.2093,
                    SantaStatus.DELIVERING_PRESENTS,  "December 25, 12:00 AM"),
            new SantaLocation("Mumbai", "India",19.0760, 72.8777,
                    SantaStatus.DELIVERING_PRESENTS, "December 25, 3:00 AM"),
            new SantaLocation("Moscow", "Russia", 55.7558, 37.6173,
                    SantaStatus.DELIVERING_PRESENTS, "December 25, 6:00 AM"),
            new SantaLocation("London", "United Kingdom", 51.5074, -0.1278,
                    SantaStatus.DELIVERING_PRESENTS, "December 25, 9:00 AM"),
            new SantaLocation("New York", "USA", 40.7128, -74.0060,
                    SantaStatus.DELIVERING_PRESENTS, "December 25, 12:00 PM"),
            new SantaLocation("Los Angeles", "USA", 34.0522, -118.2437,
                    SantaStatus.FINAL_DELIVERIES, "December 25, 3:00 PM"),
            new SantaLocation("North Pole", "Arctic", 90.0, 0.0,
                    SantaStatus.JOURNEY_COMPLETE, "December 25, 6:00 PM")
    );

    public SantaLocation getCurrentLocation() {
        LocalDateTime now = LocalDateTime.now();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();

        if (month == 12 && day == 24) {
            int hour = now.getHour();
            // Every 3 hours, Santa moves to next location
            // Hour 0-2: index 0, Hour 3-5: index 1, Hour 6-8: index 2, etc.
            int index = Math.min(hour / 3, locations.size() - 1);
            return locations.get(index);

        } else if (month == 12 && day == 25) {
            // Christmas Day - Journey complete!
            return locations.get(locations.size() - 1);

        } else {
            return new SantaLocation(
                    "North Pole Workshop",
                    "Arctic",
                    90.0,
                    0.0,
                    SantaStatus.PREPARING,
                    "December 25, 11:00 PM"
            );
        }
    }

    public String getEstimatedArrivalTime(String userCity) {
        for (int i = 0; i < locations.size(); i++) {
            SantaLocation location = locations.get(i);
            if (location.getCity().equalsIgnoreCase(userCity)) {
                // Return the fixed ETA for that location with in 3-hour window
                return location.getEstimatedArrival();
            }
        }

        // If city not in Santa's route return a generic time
        return "December 24, around midnight";
    }

    public List<SantaLocation> getAllLocations() {
        return locations;
    }

    public SantaLocation getNextLocation() {
        SantaLocation current = getCurrentLocation();

        for (int i = 0; i < locations.size() - 1; i++) {
            if (locations.get(i).getCity().equals(current.getCity())) {
                return locations.get(i + 1);
            }
        }

        return current;
    }
}