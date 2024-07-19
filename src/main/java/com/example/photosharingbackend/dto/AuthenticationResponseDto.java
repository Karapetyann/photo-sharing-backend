package com.example.photosharingbackend.dto;

public class AuthenticationResponseDto {
    private final String jwt;
    private final UserDto userDto;

    public AuthenticationResponseDto(String jwt, UserDto userDto) {
        this.jwt = jwt;
        this.userDto = userDto;
    }

    public String getJwt() {
        return jwt;
    }

    public UserDto getUser() {
        return userDto;
    }
}
