package com.scentiva.common.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BaseAppException {
    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
    }
}
