package com.santa4you.santa_delivery_service.service;

import com.santa4you.santa_delivery_service.dto.UserRegistrationRequest;
import com.santa4you.santa_delivery_service.dto.WishListUpdateRequest;
import com.santa4you.santa_delivery_service.model.User;
import com.santa4you.santa_delivery_service.model.WishList;
import com.santa4you.santa_delivery_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final TokenService tokenService;
    
    @Transactional
    public User registerUser(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists");
        }
        
        WishList wishList = new WishList();
        wishList.setItems(request.getWishItems());
        
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setAddress(request.getAddress());
        user.setWishList(wishList);
        user.setReturnGift(request.getReturnGift());
        return userRepository.save(user);
    }
    
    @Transactional
    public User updateWishList(WishListUpdateRequest request) {
        if (!tokenService.verifyToken(request.getEmail(), request.getVerificationToken())) {
            throw new IllegalArgumentException("Invalid or expired verification token");
        }
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.getWishList().setItems(request.getWishItems());
        return userRepository.save(user);
    }
    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}