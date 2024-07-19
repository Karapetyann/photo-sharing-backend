package com.example.photosharingbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeDto {
    private int id;
    private int photoId;
    private int userId;
    private String username;
    private String userProfilePhoto;
    private LocalDateTime likedAt;
}