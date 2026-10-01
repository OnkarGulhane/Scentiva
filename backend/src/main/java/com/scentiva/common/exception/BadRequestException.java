package com.scentiva.common.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseAppException {
    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "BAD_REQUEST");
    }

    public BadRequestException(String message, String errorCode) {
        super(message, HttpStatus.BAD_REQUEST, errorCode);
    }
}
