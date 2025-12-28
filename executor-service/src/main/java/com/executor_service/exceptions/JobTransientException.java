package com.executor_service.exceptions;

public class JobTransientException extends AppException{
    public JobTransientException(String message) {
        super(message);
    }

    public JobTransientException(String message, Throwable cause) {
        super(message, cause);
    }
}
