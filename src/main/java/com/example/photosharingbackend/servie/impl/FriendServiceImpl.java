package com.example.photosharingbackend.servie.impl;

import com.example.photosharingbackend.dto.FriendDto;
import com.example.photosharingbackend.dto.FriendRequestDto;
import com.example.photosharingbackend.dto.PhotoDto;
import com.example.photosharingbackend.dto.UserRequestDto;
import com.example.photosharingbackend.entity.Friend;
import com.example.photosharingbackend.entity.FriendRequest;
import com.example.photosharingbackend.entity.Photo;
import com.example.photosharingbackend.entity.User;
import com.example.photosharingbackend.entity.enums.FriendRequestStatus;
import com.example.photosharingbackend.exception.NotFountException;
import com.example.photosharingbackend.repository.FriendRepository;
import com.example.photosharingbackend.repository.FriendRequestRepository;
import com.example.photosharingbackend.security.SpringUser;
import com.example.photosharingbackend.servie.FriendService;
import com.example.photosharingbackend.servie.PhotoService;
import com.example.photosharingbackend.servie.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendServiceImpl implements FriendService {
    private final FriendRepository friendRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final PhotoService photoService;
    private final UserService userService;

    @Override
    public List<PhotoDto> getFriendPhotos(String username) {
            User user = userService.getByUsername(username);
            List<Photo> photos = photoService.getByUserId(user.getId());
            if (photos.isEmpty()) {
                throw new NotFountException(username);
            }
            return photos.stream()
                    .map(photo -> mapToPhotoDto(photo, username))
                    .collect(Collectors.toList());
    }

    private PhotoDto mapToPhotoDto(Photo photo, String username) {
        try {
            Path filePath = Paths.get("photos", photo.getUrl());
            byte[] fileContent = Files.readAllBytes(filePath);
            String base64FileContent = Base64.getEncoder().encodeToString(fileContent);
            return new PhotoDto(photo.getId(), photo.getDescription(), base64FileContent, username);
        } catch (IOException e) {
            throw new RuntimeException("Error reading photo file", e);
        }
    }

    @Override
    public void sendFriendRequest(SpringUser springUser, UserRequestDto receiverUsername) {
        User senderUser = springUser.getUser();
        User receiver = userService.getByUsername(receiverUsername.getUsername());
        processFriendRequest(senderUser, receiver);
    }

    private void processFriendRequest(User senderUser, User receiver) {
        Optional<FriendRequest> friendRequestOptional = friendRequestRepository.findByReceiverAndSender(receiver, senderUser);
        if (friendRequestOptional.isPresent()) {
            FriendRequest friendRequest = friendRequestOptional.get();
            if (friendRequest.getStatus() == FriendRequestStatus.DECLINED) {
                friendRequest.setStatus(FriendRequestStatus.PENDING);
                friendRequestRepository.save(friendRequest);
            }
        } else {
            FriendRequest friendRequest = new FriendRequest();
            friendRequest.setSender(senderUser);
            friendRequest.setReceiver(receiver);
            friendRequest.setSentDate(new Date());
            friendRequest.setStatus(FriendRequestStatus.PENDING);
            friendRequestRepository.save(friendRequest);
        }
        log.info("Friend request sent from {} to {}", senderUser.getUsername(), receiver.getUsername());
    }


    @Override
    public void respondToFriendRequest(SpringUser springUser, int requestId, FriendRequestStatus status) {
        User user = springUser.getUser();
        Optional<FriendRequest> friendRequestOptional = friendRequestRepository.findById(requestId);
        if (friendRequestOptional.isEmpty() || !friendRequestOptional.get().getReceiver().equals(user)) {
            throw new NotFountException("Friend request not found");
        }
         updateFriendRequestStatus(friendRequestOptional.get(), status);
    }

    private void updateFriendRequestStatus(FriendRequest friendRequest, FriendRequestStatus status) {
        friendRequest.setStatus(status);
        friendRequestRepository.save(friendRequest);
        if (status == FriendRequestStatus.ACCEPTED) {
            addFriends(friendRequest.getReceiver(), friendRequest.getSender());
        }
        log.info("Friend request {} processed with status {}", friendRequest.getId(), status);
    }

    private void addFriends(User receiver, User sender) {
        Friend friend1 = new Friend();
        friend1.setUser(receiver);
        friend1.setFriend(sender);

        Friend friend2 = new Friend();
        friend2.setUser(sender);
        friend2.setFriend(receiver);

        friendRepository.save(friend1);
        friendRepository.save(friend2);
    }

    @Override
    public List<FriendRequestDto> getFriendRequests(SpringUser springUser) {
            User user = springUser.getUser();
            List<FriendRequest> friendRequests = friendRequestRepository.findByReceiverAndStatus(user, FriendRequestStatus.PENDING);
            return friendRequests.stream()
                    .map(this::mapToFriendRequestDto)
                    .collect(Collectors.toList());
    }

    private FriendRequestDto mapToFriendRequestDto(FriendRequest friendRequest) {
        return new FriendRequestDto(friendRequest.getId(), friendRequest.getSender().getUsername(), friendRequest.getStatus(), friendRequest.getSentDate());
    }

    @Override
    public void acceptFriendRequest(SpringUser springUser, String username) {
        User receiver = springUser.getUser();
        User sender = userService.getByUsername(username);
        Optional<FriendRequest> friendRequestOptional = friendRequestRepository.findByReceiverAndSender(receiver, sender);
        if (friendRequestOptional.isEmpty()) {
            throw new NotFountException("Friend request not found");
        }
        processAcceptedFriendRequest(friendRequestOptional.get(), receiver, sender);
    }

    private void processAcceptedFriendRequest(FriendRequest friendRequest, User receiver, User sender) {
        friendRequest.setStatus(FriendRequestStatus.ACCEPTED);
        friendRequestRepository.save(friendRequest);
        if (!friendRepository.existsByUserAndFriend(receiver, sender)) {
            addFriends(receiver, sender);
        }else throw new NotFountException("Friend not fount");
    }


    @Override
    public void rejectFriendRequest(SpringUser springUser, String senderUsername) {
        User receiver = springUser.getUser();
        User sender = userService.getByUsername(senderUsername);
        Optional<FriendRequest> friendRequestOptional = friendRequestRepository.findByReceiverAndSender(receiver, sender);
        if (friendRequestOptional.isEmpty()) {
            throw new NotFountException("Friend request not found");
        }
        processRejectedFriendRequest(friendRequestOptional.get());
    }

    private void processRejectedFriendRequest(FriendRequest friendRequest) {
        friendRequest.setStatus(FriendRequestStatus.DECLINED);
        friendRequestRepository.save(friendRequest);
    }

    @Override
    public void deleteFriend(String username, SpringUser springUser) {
        User friend = userService.getByUsername(username);
        User user = springUser.getUser();
            friendRepository.deleteFriend(friend, user);
            friendRepository.deleteFriend(user, friend);
    }

    @Override
    public List<FriendDto> getFriends(SpringUser springUser) {
        User user = springUser.getUser();
        List<Friend> friends = friendRepository.findByUser(user);
        log.debug("Found {} friends for user {}", friends.size(), user.getUsername());
        return friends.stream()
                .map(this::createFriendDto)
                .collect(Collectors.toList());
    }

    private FriendDto createFriendDto(Friend friend) {
        FriendDto friendDto = new FriendDto();
        friendDto.setUsername(friend.getFriend().getUsername());
        friendDto.setPhoto(photoService.getPhotoContent(friend.getFriend().getProfilePictureUrl()));
        return friendDto;
    }
}
