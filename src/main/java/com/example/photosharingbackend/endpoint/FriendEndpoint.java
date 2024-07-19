package com.example.photosharingbackend.endpoint;

import com.example.photosharingbackend.dto.FriendDto;
import com.example.photosharingbackend.dto.FriendRequestDto;
import com.example.photosharingbackend.dto.UserRequestDto;
import com.example.photosharingbackend.entity.enums.FriendRequestStatus;
import com.example.photosharingbackend.security.SpringUser;
import com.example.photosharingbackend.servie.FriendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
@Slf4j
public class FriendEndpoint {

    private final FriendService friendService;

    @PostMapping("/sendFriendRequest")
    public void sendFriendRequest(@AuthenticationPrincipal SpringUser springUser, @RequestBody UserRequestDto receiverUsername) {
        log.info("User {} is sending a friend request to {}", springUser.getUsername(), receiverUsername.getUsername());
        friendService.sendFriendRequest(springUser, receiverUsername);
        log.info("Friend request sent by user {} to {}", springUser.getUsername(), receiverUsername.getUsername());
    }

    @PostMapping("/respondFriendRequest")
    public void respondToFriendRequest(@AuthenticationPrincipal SpringUser springUser, @RequestParam int requestId, @RequestParam FriendRequestStatus status) {
        log.info("User {} is responding to friend request {} with status {}", springUser.getUsername(), requestId, status);
        friendService.respondToFriendRequest(springUser, requestId, status);
        log.info("User {} responded to friend request {} with status {}", springUser.getUsername(), requestId, status);
    }

    @GetMapping("/friendRequests")
    public ResponseEntity<List<FriendRequestDto>> getFriendRequests(@AuthenticationPrincipal SpringUser springUser) {
        log.info("Fetching friend requests for user {}", springUser.getUsername());
        return ResponseEntity.ok(friendService.getFriendRequests(springUser));
    }

    @PostMapping("/acceptFriendRequest/{senderUsername}")
    public void acceptFriendRequest(@PathVariable String senderUsername, @AuthenticationPrincipal SpringUser springUser) {
        log.info("User {} is accepting friend request from {}", springUser.getUsername(), senderUsername);
        friendService.acceptFriendRequest(springUser, senderUsername);
        log.info("User {} accepted friend request from {}", springUser.getUsername(), senderUsername);
    }

    @PostMapping("/rejectFriendRequest/{senderUsername}")
    public void rejectFriendRequest(@PathVariable String senderUsername, @AuthenticationPrincipal SpringUser springUser) {
        log.info("User {} is rejecting friend request from {}", springUser.getUsername(), senderUsername);
        friendService.rejectFriendRequest(springUser, senderUsername);
        log.info("User {} rejected friend request from {}", springUser.getUsername(), senderUsername);
    }

    @GetMapping("/friends")
    public ResponseEntity<List<FriendDto>> getFriends(@AuthenticationPrincipal SpringUser springUser) {
        log.info("Fetching friends for user {}", springUser.getUsername());
        return ResponseEntity.ok(friendService.getFriends(springUser));
    }

    @DeleteMapping("/deleteFriend")
    public void deleteFriend(@RequestParam String username, @AuthenticationPrincipal SpringUser springUser) {
        log.info("User {} is deleting friend {}", springUser.getUsername(), username);
        friendService.deleteFriend(username, springUser);
        log.info("User {} deleted friend {}", springUser.getUsername(), username);
    }
}
