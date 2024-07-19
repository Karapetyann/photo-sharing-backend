package com.example.photosharingbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "failed to save photo")
public class FailedToSavePhotoException extends RuntimeException{
    public FailedToSavePhotoException(String message) {super(message);}
}
