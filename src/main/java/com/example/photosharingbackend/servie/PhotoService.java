package com.example.photosharingbackend.servie;

import com.example.photosharingbackend.dto.LikeDto;
import com.example.photosharingbackend.dto.PhotoDto;
import com.example.photosharingbackend.entity.Photo;
import com.example.photosharingbackend.security.SpringUser;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PhotoService {

    void uploadPhoto(SpringUser springUser, String description, MultipartFile file);

    List<PhotoDto> getAllPhotos(SpringUser springUser);

    void setProfilePicture(int photoId, SpringUser springUser);

    void deletePhoto(int photoId, SpringUser springUser);

    String getPhotoContent(String filename);

    PhotoDto createPhotoDto(Photo photo, String username);

    void likePhoto(Integer photoId, String username);

    int countOfLikes(Integer photoId);

    void unlikePhoto(Integer photoId, SpringUser springUser);

    List<LikeDto> getPhotoLikes(Integer photoId);

    List<Photo> getByUserId(Integer id);

    List<Integer> getUserLikedPhotos(Integer userId);
}
