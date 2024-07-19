package com.example.photosharingbackend.servie.impl;

import com.example.photosharingbackend.dto.AuthenticationRequestDto;
import com.example.photosharingbackend.dto.AuthenticationResponseDto;
import com.example.photosharingbackend.dto.FriendDto;
import com.example.photosharingbackend.dto.UserDto;
import com.example.photosharingbackend.entity.Friend;
import com.example.photosharingbackend.entity.FriendRequest;
import com.example.photosharingbackend.entity.User;
import com.example.photosharingbackend.entity.enums.UserRole;
import com.example.photosharingbackend.exception.AlreadyExistsException;
import com.example.photosharingbackend.exception.AuthenticationFailedException;
import com.example.photosharingbackend.exception.NotFountException;
import com.example.photosharingbackend.repository.FriendRepository;
import com.example.photosharingbackend.repository.FriendRequestRepository;
import com.example.photosharingbackend.repository.UserRepository;
import com.example.photosharingbackend.security.SpringUser;
import com.example.photosharingbackend.servie.PhotoService;
import com.example.photosharingbackend.servie.UserService;
import com.example.photosharingbackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final FriendRequestRepository friendRequestRepository;
    private final PhotoService photoService;
    private final FriendRepository friendRepository;

    @Override
    public void registerUser(AuthenticationRequestDto authenticationRequestDto) {
        userRepository.findByUsername(authenticationRequestDto.getUsername())
                .ifPresent(u -> {
                    throw new AlreadyExistsException("User with this username already exists");
                });
        User user = createUser(authenticationRequestDto);
        log.info("Registering new user {}", user.getUsername());
        userRepository.save(user);
    }

    private User createUser(AuthenticationRequestDto authenticationRequestDto) {
        User user = new User();
        user.setUsername(authenticationRequestDto.getUsername());
        user.setPassword(passwordEncoder.encode(authenticationRequestDto.getPassword()));
        user.setRole(UserRole.USER);
        user.setProfilePictureUrl("default.jpg");
        return user;
    }

    @Override
    public AuthenticationResponseDto loginUser(AuthenticationRequestDto authenticationRequestDto) {
        Optional<User> userOptional = userRepository.findByUsername(authenticationRequestDto.getUsername());
        if (userOptional.isEmpty() || !passwordEncoder.matches(authenticationRequestDto.getPassword(), userOptional.get().getPassword())) {
            throw new AuthenticationFailedException("Invalid username or password");
        }
        UserDto userDto = createUserDto(userOptional.get());
        String token = jwtUtil.generateToken(userOptional.get());
        log.info("Logged in user {}", userOptional.get().getUsername());
        return new AuthenticationResponseDto(token,userDto);
    }

    @Override
    public UserDto getUserProfile(String username) {
        return userRepository.findByUsername(username)
                .map(this::createUserDto)
                .orElseThrow(() -> new NotFountException(username));
    }

    private UserDto createUserDto(User user) {
        List<FriendDto> friendDtos = friendRepository.findByUser(user).stream()
                .map(this::createFriendDto)
                .collect(Collectors.toList());
        return new UserDto(user.getUsername(), photoService.getPhotoContent(user.getProfilePictureUrl()),
                photoService.getPhotoContent(user.getProfilePictureUrl()),
                friendDtos);
    }

    private FriendDto createFriendDto(Friend friend) {
        FriendDto friendDto = new FriendDto();
        friendDto.setUsername(friend.getFriend().getUsername());
        friendDto.setPhoto(photoService.getPhotoContent(friend.getFriend().getProfilePictureUrl()));
        return friendDto;
    }

    @Override
    public UserDto getCurrentUser(SpringUser springUser) {
        User user = springUser.getUser();
        return new UserDto(user.getUsername(), photoService.getPhotoContent(user.getProfilePictureUrl()),
                photoService.getPhotoContent(user.getProfilePictureUrl()), Collections.emptyList());
    }

    @Override
    public List<UserDto> getRecommendedUsers(SpringUser springUser) {
        User currentUser = springUser.getUser();
        List<User> allUsers = userRepository.findAll();
        List<User> excludedUsers = getExcludedUsers(currentUser);
        return allUsers.stream()
                .filter(user -> !excludedUsers.contains(user))
                .map(this::createUserDto)
                .filter(Objects::nonNull)
                .limit(4)
                .collect(Collectors.toList());
    }

    private List<User> getExcludedUsers(User currentUser) {
        List<User> excludedUsers = new ArrayList<>();
        excludedUsers.add(currentUser);
        excludedUsers.addAll(friendRequestRepository.findBySender(currentUser).stream()
                .map(FriendRequest::getReceiver)
                .collect(Collectors.toSet()));
        excludedUsers.addAll(friendRepository.findByUser(currentUser).stream()
                .map(Friend::getFriend)
                .collect(Collectors.toSet()));
        return excludedUsers;
    }

    @Override
    public List<UserDto> searchUsers(String name, SpringUser springUser) {
        User currentUser = springUser.getUser();
        return userRepository.findAllByUsernameContaining(name).stream()
                .filter(user -> !user.getUsername().equals(currentUser.getUsername()))
                .map(this::createUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public User getByUsername(String username) {
        Optional<User> byUsername = userRepository.findByUsername(username);
        return byUsername.orElseThrow(() -> new NotFountException("User not found"));
    }
}
