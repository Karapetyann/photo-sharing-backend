package com.example.photosharingbackend.endpoint;

import com.example.photosharingbackend.dto.LikeDto;
import com.example.photosharingbackend.dto.PhotoDto;
import com.example.photosharingbackend.security.SpringUser;
import com.example.photosharingbackend.servie.FriendService;
import com.example.photosharingbackend.servie.PhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/photo")
@RequiredArgsConstructor
@Slf4j
public class PhotoEndpoint {


    private final PhotoService photoService;
    private final FriendService friendService;

    @PostMapping("/uploadPhoto")
    public void uploadPhoto(@RequestParam("file") MultipartFile file,
                            @RequestParam("description") String description,
                            @AuthenticationPrincipal SpringUser springUser) {
        log.info("Uploading photo for user: {}", springUser.getUsername());
        photoService.uploadPhoto(springUser, description, file);
        log.info("Photo uploaded successfully for user: {}", springUser.getUsername());
    }

    @GetMapping("/photos")
    public ResponseEntity<List<PhotoDto>> getPhotos(@AuthenticationPrincipal SpringUser springUser) {
        log.info("Fetching all photos for user: {}", springUser.getUsername());
        return ResponseEntity.ok(photoService.getAllPhotos(springUser));
    }

    @PostMapping("/setProfilePicture/{photoId}")
    public void setProfilePicture(@PathVariable int photoId, @AuthenticationPrincipal SpringUser springUser) {
        log.info("Setting profile picture for user: {}", springUser.getUsername());
        photoService.setProfilePicture(photoId, springUser);
        log.info("Profile picture set for user: {}", springUser.getUsername());
    }

    @GetMapping("/friendPhotos/{username}")
    public ResponseEntity<List<PhotoDto>> getFriendPhotos(@PathVariable String username) {
        log.info("Fetching friend photos for username: {}", username);
        return ResponseEntity.ok(friendService.getFriendPhotos(username));
    }

    @PostMapping("/likePhoto")
    public void likePhoto(@RequestBody LikeDto likeDto) {
        log.info("Liking photo with ID: {} by user with ID: {}", likeDto.getPhotoId(), likeDto.getUsername());
        photoService.likePhoto(likeDto.getPhotoId(), likeDto.getUsername());
        log.info("Photo liked successfully with ID: {} by user with ID: {}", likeDto.getPhotoId(), likeDto.getUserId());
    }

    @GetMapping("/photos/likes/{photoId}")
    public ResponseEntity<List<LikeDto>> getLikesForPhoto(@PathVariable Integer photoId) {
        log.info("Fetching likes for photo ID: {}", photoId);
        return ResponseEntity.ok(photoService.getPhotoLikes(photoId));
    }

    @GetMapping("/photo/likes/count/{photoId}")
    public ResponseEntity<Integer> getLikeCountForPhoto(@PathVariable Integer photoId) {
        log.info("Fetching like count for photo ID: {}", photoId);
        return ResponseEntity.ok(photoService.countOfLikes(photoId));
    }

    @DeleteMapping("/unlikes")
    public void unlikePhoto(@RequestParam Integer photoId, @AuthenticationPrincipal SpringUser springUser) {
        log.info("Unliking photo with ID: {} by user with ID: {}", photoId, springUser.getUsername());
        photoService.unlikePhoto(photoId, springUser);
        log.info("Photo unliked successfully with ID: {} by user with ID: {}", photoId, springUser.getUsername());
    }

    @DeleteMapping("/deletePhoto/{photoId}")
    public void deletePhoto(@PathVariable int photoId, @AuthenticationPrincipal SpringUser springUser) {
        log.info("Deleting photo with ID: {} by user: {}", photoId, springUser.getUsername());
        photoService.deletePhoto(photoId, springUser);
        log.info("Photo deleted successfully with ID: {} by user: {}", photoId, springUser.getUsername());
    }

    @GetMapping("/userLikes/{userId}")
    public ResponseEntity<List<Integer>> getUserLikedPhotos(@PathVariable Integer userId) {
        log.info("Fetching liked photos for user ID: {}", userId);
        return ResponseEntity.ok(photoService.getUserLikedPhotos(userId));
    }
}
