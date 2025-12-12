package com.santa4you.santa_delivery_service.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @NotBlank(message = "Street address is required")
    @Size(min = 10, max = 200, message = "Address must be between 10 and 200 characters")
    private String street;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State/Province is required")
    private String state;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "Postal code is required")
    @Size(min = 3, max = 10, message = "Postal code must be between 3 and 10 characters")
    @Pattern(regexp = "^[A-Za-z0-9\\-\\s]+$",
            message = "Postal code can only contain letters, numbers, hyphens, and spaces")
    private String postalCode;

    @NotBlank(message = "Apartment/Flat No. is required for Santa to precisely deliver your wish list")
    private String apartment;
}
