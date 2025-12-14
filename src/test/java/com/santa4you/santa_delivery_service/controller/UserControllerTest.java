package com.santa4you.santa_delivery_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.santa4you.santa_delivery_service.dto.TokenRequest;
import com.santa4you.santa_delivery_service.dto.UserRegistrationRequest;
import com.santa4you.santa_delivery_service.dto.WishListUpdateRequest;
import com.santa4you.santa_delivery_service.model.Address;
import com.santa4you.santa_delivery_service.model.ReturnGift;
import com.santa4you.santa_delivery_service.model.User;
import com.santa4you.santa_delivery_service.model.WishList;
import com.santa4you.santa_delivery_service.service.TokenService;
import com.santa4you.santa_delivery_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private TokenService tokenService;

    private UserRegistrationRequest validRequest;
    private User mockUser;

    @BeforeEach
    void setUp() {
        Address address = new Address(
                "123 Marine Drive",
                "Mumbai",
                "Maharashtra",
                "India",
                "12345", "404"
        );

        validRequest = new UserRegistrationRequest();
        validRequest.setName("Test User");
        validRequest.setEmail("testuser@gsail.com");
        validRequest.setAddress(address);
        validRequest.setWishItems(Arrays.asList("PS 5", "Laptop", "Bike"));
        validRequest.setReturnGift(ReturnGift.COOKIES);

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("Test User");
        mockUser.setEmail("testuser@gsail.com");
        mockUser.setAddress(address);
        mockUser.setReturnGift(ReturnGift.COOKIES);
        
        WishList wishList = new WishList();
        wishList.setItems(Arrays.asList("PS 5", "Laptop", "Bike"));
        mockUser.setWishList(wishList);
    }

    @Test
    void registerUser_WithValidRequest_ShouldReturnCreated() throws Exception {
        when(userService.registerUser(any(UserRegistrationRequest.class))).thenReturn(mockUser);

        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Test User"))
            .andExpect(jsonPath("$.email").value("testuser@gsail.com"))
            .andExpect(jsonPath("$.returnGift").value("COOKIES"));

        verify(userService, times(1)).registerUser(any(UserRegistrationRequest.class));
    }

    @Test
    void registerUser_WithExistingEmail_ShouldReturnBadRequest() throws Exception {
        when(userService.registerUser(any(UserRegistrationRequest.class)))
            .thenThrow(new IllegalArgumentException("User with this email already exists"));

        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("User with this email already exists"));
    }

    @Test
    void requestToken_WithValidEmail_ShouldReturnOk() throws Exception {
        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setEmail("testuser@gsail.com");

        when(tokenService.generateAndSendToken("testuser@gsail.com")).thenReturn("123456");

        mockMvc.perform(post("/api/v1/users/request-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tokenRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Verification token sent to testuser@gsail.com"));

        verify(tokenService, times(1)).generateAndSendToken("testuser@gsail.com");
    }


    @Test
    void updateWishList_WithValidToken_ShouldReturnOk() throws Exception {
        WishListUpdateRequest updateRequest = new WishListUpdateRequest();
        updateRequest.setEmail("testuser@gsail.com");
        updateRequest.setWishItems(Arrays.asList("Book", "Toy", "Game"));
        updateRequest.setVerificationToken("123456");

        when(userService.updateWishList(any(WishListUpdateRequest.class))).thenReturn(mockUser);

        mockMvc.perform(put("/api/v1/users/wishlist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("testuser@gsail.com"));

        verify(userService, times(1)).updateWishList(any(WishListUpdateRequest.class));
    }

    @Test
    void updateWishList_WithInvalidToken_ShouldReturnBadRequest() throws Exception {
        WishListUpdateRequest updateRequest = new WishListUpdateRequest();
        updateRequest.setEmail("testuser@gsail.com");
        updateRequest.setWishItems(Arrays.asList("Book", "Toy", "Game"));
        updateRequest.setVerificationToken("invalid");

        when(userService.updateWishList(any(WishListUpdateRequest.class)))
            .thenThrow(new IllegalArgumentException("Invalid or expired verification token"));

        mockMvc.perform(put("/api/v1/users/wishlist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Invalid or expired verification token"));
    }


    @Test
    void getUser_WithExistingEmail_ShouldReturnUser() throws Exception {
        when(userService.getUserByEmail("testuser@gsail.com")).thenReturn(mockUser);

        mockMvc.perform(get("/api/v1/users/testuser@gsail.com"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Test User"))
            .andExpect(jsonPath("$.email").value("testuser@gsail.com"));

        verify(userService, times(1)).getUserByEmail("testuser@gsail.com");
    }

    @Test
    void getUser_WithNonExistentEmail_ShouldReturnNotFound() throws Exception {
        when(userService.getUserByEmail("nonexistent@gsail.com"))
            .thenThrow(new IllegalArgumentException("User not found"));

        mockMvc.perform(get("/api/v1/users/nonexistent@gsail.com"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("User not found"));
    }
}