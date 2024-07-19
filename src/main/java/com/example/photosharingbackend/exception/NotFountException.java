package com.example.photosharingbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "NOT FOUND")
public class NotFountException extends RuntimeException{
    public NotFountException(String message) {super(message);}
}
