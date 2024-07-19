package com.example.photosharingbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "you already liked")
public class AlreadyExistsException extends RuntimeException{
    public AlreadyExistsException(String message) {super(message);}
}
