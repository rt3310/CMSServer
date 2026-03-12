package com.malgn.cmsserver.common.controller;

import com.malgn.cmsserver.support.exception.AppException;
import com.malgn.cmsserver.support.exception.ErrorCode;
import com.malgn.cmsserver.support.exception.ErrorType;
import com.malgn.cmsserver.support.response.AppApiResponse;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiControllerAdvice {

    @NullMarked
    @ExceptionHandler(AppException.class)
    public ResponseEntity<AppApiResponse<Void>> handleAppException(AppException e) {
        printAppExceptionLog(e);

        return new ResponseEntity<>(AppApiResponse.error(e.getErrorType()), e.getErrorType().getStatus());
    }

    @NullMarked
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AppApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("[ValidationException]: {}", e.getMessage());

        String errorMessage = ErrorType.INVALID_INPUT.getMessage();

        FieldError fieldError = e.getBindingResult().getFieldError();
        if (fieldError != null) {
                errorMessage = fieldError.getField() + ": " + fieldError.getDefaultMessage();
        }

        return new ResponseEntity<>(
                AppApiResponse.error(ErrorType.INVALID_INPUT, errorMessage),
                HttpStatus.BAD_REQUEST
        );
    }

    @NullMarked
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<AppApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("[HttpMessageNotReadableException]: {}", e.getMessage());

        return new ResponseEntity<>(
                AppApiResponse.error(ErrorType.INVALID_REQUEST_BODY),
                HttpStatus.BAD_REQUEST
        );
    }

    @NullMarked
    @ExceptionHandler({OptimisticLockException.class, ObjectOptimisticLockingFailureException.class})
    public ResponseEntity<AppApiResponse<Void>> handleOptimisticLockException(Exception e) {
        log.warn("[OptimisticLockException]: {}", e.getMessage());

        return new ResponseEntity<>(
                AppApiResponse.error(ErrorType.CONFLICT),
                HttpStatus.CONFLICT
        );
    }

    @NullMarked
    @ExceptionHandler(Exception.class)
    public ResponseEntity<AppApiResponse<Void>> handleException(Exception e) {
        printExceptionLog(e);
        return new ResponseEntity<>(AppApiResponse.error(ErrorType.SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void printAppExceptionLog(AppException e) {
        String className = e.getStackTrace()[0].getClassName();
        String methodName = e.getStackTrace()[0].getMethodName();
        int lineNumber = e.getStackTrace()[0].getLineNumber();
        int status = e.getErrorType().getStatus().value();
        ErrorCode errorCode = e.getErrorType().getErrorCode();
        String message = e.getMessage();

        switch (e.getErrorType().getLogLevel()) {
            case ERROR ->
                    log.error("[AppException]: class={} | method={} | line={} | status={} | errorCode={} | message={}",
                            className, methodName, lineNumber, status, errorCode, message);
            case WARN ->
                    log.warn("[AppException]: class={} | method={} | line={} | status={} | errorCode={} | message={}",
                            className, methodName, lineNumber, status, errorCode, message);
            default ->
                    log.info("[AppException]: class={} | method={} | line={} | status={} | errorCode={} | message={}",
                            className, methodName, lineNumber, status, errorCode, message);
        }
    }

    private void printExceptionLog(Exception e) {
        String className = e.getStackTrace()[0].getClassName();
        String methodName = e.getStackTrace()[0].getMethodName();
        int lineNumber = e.getStackTrace()[0].getLineNumber();
        String message = e.getMessage();

        log.error("[Exception]: class={} | method={} | line={} | message={}",
                className, methodName, lineNumber, message);
    }
}
