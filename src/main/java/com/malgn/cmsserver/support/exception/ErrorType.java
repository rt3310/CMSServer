package com.malgn.cmsserver.support.exception;

import lombok.Getter;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorType {
    ;

    private final HttpStatus status;
    private final ErrorCode errorCode;
    private final String message;
    private final LogLevel logLevel;

    ErrorType(HttpStatus status, ErrorCode errorCode, String message, LogLevel logLevel) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
        this.logLevel = logLevel;
    }
}
