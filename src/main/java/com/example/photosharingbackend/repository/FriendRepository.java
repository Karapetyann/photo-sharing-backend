package com.example.photosharingbackend.repository;

import com.example.photosharingbackend.entity.Friend;
import com.example.photosharingbackend.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Integer> {

    List<Friend> findByUser(User user);

    boolean existsByUserAndFriend(User receiver, User sender);

    @Transactional
    @Modifying
    @Query("DELETE FROM Friend f WHERE f.user = :user AND f.friend = :friend")
    void deleteFriend(User user, User friend);
}