package com.santa4you.santa_delivery_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SantaLocation {
    private String city;
    private String country;
    private double latitude;
    private double longitude;
    private SantaStatus status;
    private String estimatedArrival;
}