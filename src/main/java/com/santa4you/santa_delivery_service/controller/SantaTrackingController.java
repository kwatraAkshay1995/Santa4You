package com.santa4you.santa_delivery_service.controller;

import com.santa4you.santa_delivery_service.model.SantaLocation;
import com.santa4you.santa_delivery_service.service.SantaTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/santa")
@RequiredArgsConstructor
public class SantaController {

    private final SantaTrackingService santaTrackingService;

    @GetMapping("/location")
    public ResponseEntity<SantaLocation> getCurrentLocation() {
        return ResponseEntity.ok(santaTrackingService.getCurrentLocation());
    }

    @GetMapping("/eta")
    public ResponseEntity<?> getEstimatedArrival(@RequestParam String city) {
        String eta = santaTrackingService.getEstimatedArrivalTime(city);
        return ResponseEntity.ok(Map.of(
                "city", city,
                "estimatedArrival", eta,
                "message", "Santa will arrive at " + city + " around " + eta
        ));
    }

    @GetMapping("/route")
    public ResponseEntity<List<SantaLocation>> getRoute() {
        return ResponseEntity.ok(santaTrackingService.getAllLocations());
    }

    @GetMapping("/next")
    public ResponseEntity<SantaLocation> getNextLocation() {
        return ResponseEntity.ok(santaTrackingService.getNextLocation());
    }
}