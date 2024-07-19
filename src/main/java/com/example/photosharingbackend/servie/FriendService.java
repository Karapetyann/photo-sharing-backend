package com.example.photosharingbackend.servie;

import com.example.photosharingbackend.dto.FriendDto;
import com.example.photosharingbackend.dto.FriendRequestDto;
import com.example.photosharingbackend.dto.PhotoDto;
import com.example.photosharingbackend.dto.UserRequestDto;
import com.example.photosharingbackend.entity.enums.FriendRequestStatus;
import com.example.photosharingbackend.security.SpringUser;

import java.util.List;

public interface FriendService {

    List<PhotoDto> getFriendPhotos(String username);

    void sendFriendRequest(SpringUser springUser, UserRequestDto receiverUsername);

    void respondToFriendRequest(SpringUser springUser, int requestId, FriendRequestStatus status);

    List<FriendRequestDto> getFriendRequests(SpringUser springUser);

    void acceptFriendRequest(SpringUser springUser, String username);

    void rejectFriendRequest(SpringUser springUser, String username);

    List<FriendDto> getFriends(SpringUser springUser);

    void deleteFriend(String username, SpringUser springUser);

}
