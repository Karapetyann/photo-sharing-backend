package com.example.photosharingbackend.servie.impl;

import com.example.photosharingbackend.dto.LikeDto;
import com.example.photosharingbackend.dto.PhotoDto;
import com.example.photosharingbackend.entity.Like;
import com.example.photosharingbackend.entity.Photo;
import com.example.photosharingbackend.entity.User;
import com.example.photosharingbackend.exception.FailedToSavePhotoException;
import com.example.photosharingbackend.exception.AlreadyExistsException;
import com.example.photosharingbackend.exception.NotFountException;
import com.example.photosharingbackend.repository.LikeRepository;
import com.example.photosharingbackend.repository.PhotoRepository;
import com.example.photosharingbackend.repository.UserRepository;
import com.example.photosharingbackend.security.SpringUser;
import com.example.photosharingbackend.servie.PhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoServiceImpl implements PhotoService {

    private final PhotoRepository photoRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    @Override
    public void uploadPhoto(SpringUser springUser, String description, MultipartFile file) {
        try {
            User user = springUser.getUser();
            String filename = savePhotoFile(file);
            savePhotoRecord(description, user, filename);
        } catch (IOException e) {
            throw new FailedToSavePhotoException("failed to save photo");
        }
    }

    private String savePhotoFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String filenameWithTimestamp = System.currentTimeMillis() + "_" + originalFilename;
        String directory = "photos/";
        Path filepath = Paths.get(directory, filenameWithTimestamp);
        Files.createDirectories(filepath.getParent());
        Files.write(filepath, file.getBytes());
        return filenameWithTimestamp;
    }

    private void savePhotoRecord(String description, User user, String filename) {
        Photo photo = new Photo();
        photo.setUrl(filename);
        photo.setDescription(description);
        photo.setUploadDate(new Date());
        photo.setUser(user);
        photoRepository.save(photo);
        log.info("Photo saved successfully: {}", filename);
    }

    @Override
    public List<PhotoDto> getAllPhotos(SpringUser springUser) {
        User user = springUser.getUser();
        return photoRepository.findByUserId(user.getId()).stream()
                .map(photo -> createPhotoDto(photo, user.getUsername()))
                .collect(Collectors.toList());
    }

    @Override
    public void setProfilePicture(int photoId, SpringUser springUser) {
        User user = springUser.getUser();
        photoRepository.findById(photoId)
                .map(photo -> updateProfilePicture(user, photo))
                .orElseThrow(() -> new NotFountException("Photo not found"));
    }

    private boolean updateProfilePicture(User user, Photo photo) {
        user.setProfilePictureUrl(photo.getUrl());
        userRepository.save(user);
        return true;
    }

    @Override
    public void deletePhoto(int photoId, SpringUser springUser) {
        User user = springUser.getUser();
         photoRepository.findById(photoId)
                .map(photo -> deletePhotoIfUser(photo, user))
                .orElseThrow(() -> new NotFountException("Photo not found"));
    }

    private boolean deletePhotoIfUser(Photo photo, User user) {
        if (photo.getUser().getId() != user.getId()) return false;
        try {
            photoRepository.delete(photo);
            Files.deleteIfExists(Paths.get("photos", photo.getUrl()));
            log.info("Photo deleted successfully: {}", photo.getUrl());
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getPhotoContent(String filename) {
        try {
            Path filePath = Path.of("photos/", filename);
            byte[] fileContent = Files.readAllBytes(filePath);
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            throw new NotFountException("photo not found");
        }
    }

    @Override
    public PhotoDto createPhotoDto(Photo photo, String username) {
        String base64FileContent = getPhotoContent(photo.getUrl());
        return new PhotoDto(photo.getId(), photo.getDescription(), base64FileContent, username);
    }

    @Override
    public int countOfLikes(Integer photoId) {
        return photoRepository.findById(photoId)
                .map(photo -> likeRepository.findByPhotoId(photo.getId()).size())
                .orElse(0);
    }

    @Override
    public void unlikePhoto(Integer photoId, SpringUser springUser) {
        Optional<Photo> photo = photoRepository.findById(photoId);
        User user = springUser.getUser();
        if (photo.isPresent()) {
            likeRepository.findByPhotoAndUser(photo.get(), user)
                    .ifPresent(likeRepository::delete);
        }else throw new NotFountException("Like not fount");
    }

    @Override
    public List<LikeDto> getPhotoLikes(Integer photoId) {
        return photoRepository.findById(photoId)
                .map(photo -> {
                        return likeRepository.findByPhotoId(photo.getId()).stream()
                                .map(this::convertToDto)
                                .collect(Collectors.toList());
                })
                .orElseThrow(()->new NotFountException("Photo not found"));
    }

    @Override
    public void likePhoto(Integer photoId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFountException("User not found"));
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new NotFountException("Photo not found"));
        if (likeRepository.findByPhotoAndUser(photo, user).isPresent()) throw new AlreadyExistsException("you already liked");
        Like newLike = new Like();
        newLike.setPhoto(photo);
        newLike.setUser(user);
        newLike.setLikedAt(LocalDateTime.now());
        likeRepository.save(newLike);
    }

    private LikeDto convertToDto(Like like) {
        try {
            Optional<User> userProfileOptional = userRepository.findByUsername(like.getUser().getUsername());
            if (userProfileOptional.isEmpty()) {
                return null;
            }
            User userProfile = userProfileOptional.get();
            Photo photo = like.getPhoto();
            String photoContent = getPhotoContent(photo.getUrl());
            return new LikeDto(
                    like.getId(),
                    like.getPhoto().getId(),
                    like.getUser().getId(),
                    userProfile.getUsername(),
                    photoContent,
                    like.getLikedAt()
            );
        } catch (Exception e) {
            System.err.println("Error converting Like to LikeDto: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<Photo> getByUserId(Integer userId) {
        return photoRepository.findByUserId(userId);
    }


    @Override
    public List<Integer> getUserLikedPhotos(Integer userId) {
        return likeRepository.findByUserId(userId);
    }
}
