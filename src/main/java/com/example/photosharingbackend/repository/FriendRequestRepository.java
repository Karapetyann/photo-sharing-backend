package com.example.photosharingbackend.repository;

import com.example.photosharingbackend.entity.FriendRequest;
import com.example.photosharingbackend.entity.User;
import com.example.photosharingbackend.entity.enums.FriendRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Integer> {

    List<FriendRequest> findByReceiver(User receiver);

    List<FriendRequest> findBySender(User sender);

    Optional<FriendRequest> findByReceiverAndSender(User receiver, User sender);

    List<FriendRequest> findByReceiverAndStatus(User user, FriendRequestStatus friendRequestStatus);
}
