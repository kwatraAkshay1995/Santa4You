package com.santa4you.santa_delivery_service.dto;

import com.santa4you.santa_delivery_service.model.Address;
import com.santa4you.santa_delivery_service.model.ReturnGift;
import com.santa4you.santa_delivery_service.model.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UserRegistrationRequest {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @Valid
    @NotNull(message = "Address is required")
    private Address address;
    
    @Size(min = 3, max = 3, message = "Must provide exactly 3 wish items")
    private List<@NotBlank(message = "Wish item cannot be blank") String> wishItems;
    
    @NotNull(message = "Return gift is required (MILK or COOKIES)")
    private ReturnGift returnGift;
}