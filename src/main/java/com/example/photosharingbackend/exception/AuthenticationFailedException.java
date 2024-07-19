package com.example.photosharingbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "failed to save photo")
public class AuthenticationFailedException extends RuntimeException{
    public AuthenticationFailedException(String message) {super(message);}
}
