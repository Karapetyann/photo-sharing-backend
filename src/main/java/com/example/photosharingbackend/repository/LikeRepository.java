package com.example.photosharingbackend.repository;

import com.example.photosharingbackend.entity.Like;
import com.example.photosharingbackend.entity.Photo;
import com.example.photosharingbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Integer> {

    Optional<Like> findByPhotoAndUser(Photo photo, User user);

    List<Like> findByPhotoId(int photoId);

    List<Integer> findByUserId(Integer id);
}
