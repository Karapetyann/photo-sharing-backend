package com.example.photosharingbackend.dto;

import com.example.photosharingbackend.entity.enums.FriendRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestDto {
    private int id;
    private String senderUsername;
    private FriendRequestStatus status;
    private Date sentDate;
}