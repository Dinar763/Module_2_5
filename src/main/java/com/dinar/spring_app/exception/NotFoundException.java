package com.dinar.spring_app.exception;

public class NotFoundException extends ServiceException {
    public NotFoundException(String message) {
        super(message);
    }
}
