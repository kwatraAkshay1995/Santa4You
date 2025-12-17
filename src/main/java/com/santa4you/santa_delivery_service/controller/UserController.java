package com.santa4you.santa_delivery_service.controller;

import com.santa4you.santa_delivery_service.dto.TokenRequest;
import com.santa4you.santa_delivery_service.dto.UserRegistrationRequest;
import com.santa4you.santa_delivery_service.dto.WishListUpdateRequest;
import com.santa4you.santa_delivery_service.model.User;
import com.santa4you.santa_delivery_service.service.TokenService;
import com.santa4you.santa_delivery_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final TokenService tokenService;
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        User user = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
    
    @PostMapping("/request-token")
    public ResponseEntity<?> requestToken(@Valid @RequestBody TokenRequest request) {
        tokenService.generateAndSendToken(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "Verification token sent to " + request.getEmail()));
    }
    
    @PutMapping("/wishlist")
    public ResponseEntity<?> updateWishList(@Valid @RequestBody WishListUpdateRequest request) {
        User user = userService.updateWishList(request);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/{email}")
    public ResponseEntity<?> getUser(@PathVariable String email) {
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }
}