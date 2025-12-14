package com.santa4you.santa_delivery_service.service;

import com.santa4you.santa_delivery_service.dto.UserRegistrationRequest;
import com.santa4you.santa_delivery_service.dto.WishListUpdateRequest;
import com.santa4you.santa_delivery_service.model.Address;
import com.santa4you.santa_delivery_service.model.ReturnGift;
import com.santa4you.santa_delivery_service.model.User;
import com.santa4you.santa_delivery_service.model.WishList;
import com.santa4you.santa_delivery_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UserService userService;

    private UserRegistrationRequest validRequest;
    private Address validAddress;

    @BeforeEach
    void setUp() {
        validAddress = new Address(
            "123 Marine Drive",
            "Mumbai",
            "Maharashtra",
            "India",
            "12345", "404"
        );

        validRequest = new UserRegistrationRequest();
        validRequest.setName("Test User");
        validRequest.setEmail("testuser@gsail.com");
        validRequest.setAddress(validAddress);
        validRequest.setWishItems(Arrays.asList("PS 5", "Laptop", "Bike"));
        validRequest.setReturnGift(ReturnGift.COOKIES);
    }

    @Test
    void registerUser_WithValidRequest_ShouldSaveUser() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.registerUser(validRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test User");
        assertThat(result.getEmail()).isEqualTo("testuser@gsail.com");
        assertThat(result.getReturnGift()).isEqualTo(ReturnGift.COOKIES);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_ShouldCreateWishListWithThreeItems() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        userService.registerUser(validRequest);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        
        assertThat(savedUser.getWishList()).isNotNull();
        assertThat(savedUser.getWishList().getItems()).hasSize(3);
        assertThat(savedUser.getWishList().getItems())
            .containsExactly("PS 5", "Laptop", "Bike");
    }

    @Test
    void registerUser_ShouldSetAddressCorrectly() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        userService.registerUser(validRequest);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        
        assertThat(savedUser.getAddress()).isNotNull();
        assertThat(savedUser.getAddress().getStreet()).isEqualTo("123 Marine Drive");
        assertThat(savedUser.getAddress().getCity()).isEqualTo("Mumbai");
        assertThat(savedUser.getAddress().getPostalCode()).isEqualTo("12345");
    }

    @Test
    void registerUser_WithExistingEmail_ShouldThrowException() {
        when(userRepository.existsByEmail("testuser@gsail.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(validRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User with this email already exists");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateWishList_WithValidToken_ShouldUpdateWishList() {
        WishListUpdateRequest updateRequest = new WishListUpdateRequest();
        updateRequest.setEmail("testuser@gsail.com");
        updateRequest.setWishItems(Arrays.asList("Book", "Toy", "Game"));
        updateRequest.setVerificationToken("123456");

        User existingUser = new User();
        existingUser.setEmail("testuser@gsail.com");
        existingUser.setName("Test User");
        WishList wishList = new WishList();
        wishList.setItems(Arrays.asList("Old Item 1", "Old Item 2", "Old Item 3"));
        existingUser.setWishList(wishList);

        when(tokenService.verifyToken("testuser@gsail.com", "123456")).thenReturn(true);
        when(userRepository.findByEmail("testuser@gsail.com")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.updateWishList(updateRequest);

        assertThat(result.getWishList().getItems())
            .containsExactly("Book", "Toy", "Game");
        verify(tokenService, times(1)).verifyToken("testuser@gsail.com", "123456");
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void updateWishList_WithInvalidToken_ShouldThrowException() {
        WishListUpdateRequest updateRequest = new WishListUpdateRequest();
        updateRequest.setEmail("testuser@gsail.com");
        updateRequest.setWishItems(Arrays.asList("Book", "Toy", "Game"));
        updateRequest.setVerificationToken("invalid");

        when(tokenService.verifyToken("testuser@gsail.com", "invalid")).thenReturn(false);

        assertThatThrownBy(() -> userService.updateWishList(updateRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid or expired verification token");

        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateWishList_WithNonExistentUser_ShouldThrowException() {
        WishListUpdateRequest updateRequest = new WishListUpdateRequest();
        updateRequest.setEmail("nonexistent@example.com");
        updateRequest.setWishItems(Arrays.asList("Book", "Toy", "Game"));
        updateRequest.setVerificationToken("123456");

        when(tokenService.verifyToken("nonexistent@example.com", "123456")).thenReturn(true);
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateWishList(updateRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User not found");

        verify(userRepository, never()).save(any(User.class));
    }
}