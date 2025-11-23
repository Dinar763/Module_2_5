package com.dinar.spring_app.exception;

public class ValidationException extends ServiceException {
    public ValidationException(String message) {
        super(message);
    }
}
