package com.user_service.user_service.exceptions;

public class BadRequestException extends AppException{
    public BadRequestException(String message) {
        super(message);
    }
}
