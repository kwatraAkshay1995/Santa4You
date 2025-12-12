package com.santa4you.santa_delivery_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class WishListUpdateRequest {
    
    @NotBlank(message = "Email is required")
    private String email;
    
    @Size(min = 3, max = 3, message = "Must provide exactly 3 wish items")
    private List<@NotBlank(message = "Wish item cannot be blank") String> wishItems;
    
    @NotBlank(message = "Verification token is required")
    private String verificationToken;
}