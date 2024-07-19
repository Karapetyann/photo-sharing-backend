package com.example.photosharingbackend.endpoint;

import com.example.photosharingbackend.dto.AuthenticationRequestDto;
import com.example.photosharingbackend.dto.UserDto;
import com.example.photosharingbackend.security.SpringUser;
import com.example.photosharingbackend.servie.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class UserEndpoint {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequestDto authenticationRequestDto) {
        log.info("Login attempt for user: {}", authenticationRequestDto.getUsername());
        ResponseEntity<?> response = ResponseEntity.ok(userService.loginUser(authenticationRequestDto));
        log.info("Login successful for user: {}", authenticationRequestDto.getUsername());
        return response;
    }

    @PostMapping("/register")
    public void registerUser(@RequestBody AuthenticationRequestDto authenticationRequestDto) {
        log.info("Registration attempt for user: {}", authenticationRequestDto.getUsername());
        userService.registerUser(authenticationRequestDto);
        log.info("User registered: {}", authenticationRequestDto.getUsername());
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<UserDto> getUserProfile(@PathVariable String username) {
        log.info("Fetching profile for user: {}", username);
        UserDto userProfile = userService.getUserProfile(username);
        log.info("Profile fetched for user: {}", username);
        return ResponseEntity.ok(userProfile);
    }

    @GetMapping("/currentUser")
    public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal SpringUser springUser) {
        log.info("Fetching current user profile for user: {}", springUser.getUsername());
        UserDto currentUser = userService.getCurrentUser(springUser);
        log.info("Current user profile fetched for user: {}", springUser.getUsername());
        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("/recommendedUsers")
    public ResponseEntity<List<UserDto>> getRecommendedUsers(@AuthenticationPrincipal SpringUser springUser) {
        log.info("Fetching recommended users for user: {}", springUser.getUsername());
        List<UserDto> recommendedUsers = userService.getRecommendedUsers(springUser);
        log.info("Recommended users fetched for user: {}", springUser.getUsername());
        return ResponseEntity.ok(recommendedUsers);
    }

    @GetMapping("/searchUsers/{name}")
    public ResponseEntity<List<UserDto>> getUsersByQuery(@PathVariable String name, @AuthenticationPrincipal SpringUser springUser) {
        log.info("Searching users by name: {} for user: {}", name, springUser.getUsername());
        List<UserDto> users = userService.searchUsers(name, springUser);
        log.info("Users search completed by name: {} for user: {}", name, springUser.getUsername());
        return ResponseEntity.ok(users);
    }
}