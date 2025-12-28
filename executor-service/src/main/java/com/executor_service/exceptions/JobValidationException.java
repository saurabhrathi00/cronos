package com.executor_service.exceptions;

public class JobValidationException extends AppException {
    public JobValidationException(String message) {
        super(message);
    }

    public JobValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
