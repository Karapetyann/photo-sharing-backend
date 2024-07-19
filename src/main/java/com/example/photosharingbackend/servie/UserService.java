package com.example.photosharingbackend.servie;

import com.example.photosharingbackend.dto.AuthenticationRequestDto;
import com.example.photosharingbackend.dto.AuthenticationResponseDto;
import com.example.photosharingbackend.dto.UserDto;
import com.example.photosharingbackend.entity.User;
import com.example.photosharingbackend.security.SpringUser;

import java.util.List;

public interface UserService {

    void registerUser(AuthenticationRequestDto authenticationRequestDto);

    AuthenticationResponseDto loginUser(AuthenticationRequestDto authenticationRequestDto);

    UserDto getUserProfile(String username);

    UserDto getCurrentUser(SpringUser springUser);

    List<UserDto> getRecommendedUsers(SpringUser springUser);

    List<UserDto> searchUsers(String name, SpringUser springUser);

    User getByUsername(String username);

}

