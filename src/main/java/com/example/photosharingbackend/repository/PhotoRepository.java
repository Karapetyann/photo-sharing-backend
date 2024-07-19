package com.example.photosharingbackend.repository;

import com.example.photosharingbackend.entity.Like;
import com.example.photosharingbackend.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Integer> {

    List<Photo> findByUserId(Integer userId);
}
