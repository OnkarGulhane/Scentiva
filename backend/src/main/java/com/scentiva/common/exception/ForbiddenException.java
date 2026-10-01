package com.scentiva.common.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseAppException {
    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN, "ACCESS_DENIED");
    }
}
