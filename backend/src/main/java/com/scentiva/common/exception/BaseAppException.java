package com.scentiva.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base Application Exception for all domain business errors.
 */
@Getter
public abstract class BaseAppException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    public BaseAppException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public BaseAppException(String message, Throwable cause, HttpStatus status, String errorCode) {
        super(message, cause);
        this.status = status;
        this.errorCode = errorCode;
    }
}
