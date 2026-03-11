package com.malgn.cmsserver.support.exception;

public record ErrorMessage(
        String errorCode,
        String message
) {
    public static ErrorMessage of(ErrorType errorType) {
        return new ErrorMessage(errorType.getErrorCode().name(), errorType.getMessage());
    }

    public static ErrorMessage of(ErrorType errorType, String message) {
        return new ErrorMessage(errorType.getErrorCode().name(), message);
    }
}
