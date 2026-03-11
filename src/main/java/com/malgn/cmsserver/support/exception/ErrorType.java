package com.malgn.cmsserver.support.exception;

import lombok.Getter;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorType {
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", LogLevel.ERROR),
    NOT_FOUND_DATA(HttpStatus.BAD_REQUEST, ErrorCode.E400, "해당 데이터를 찾을 수 없습니다.", LogLevel.WARN),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, ErrorCode.E400, "입력값이 올바르지 않습니다.", LogLevel.WARN),
    INVALID_REQUEST_BODY(HttpStatus.BAD_REQUEST, ErrorCode.E400, "요청 본문을 읽을 수 없습니다.", LogLevel.WARN),
    FAILED_AUTH(HttpStatus.BAD_REQUEST, ErrorCode.E400, "인증에 실패했습니다.", LogLevel.WARN),
    REQUIRED_AUTH(HttpStatus.UNAUTHORIZED, ErrorCode.E401, "리소스에 접근하기 위한 인증이 필요합니다.", LogLevel.WARN),
    FORBIDDEN(HttpStatus.FORBIDDEN, ErrorCode.E403, "해당 리소스에 대한 권한이 없습니다.", LogLevel.WARN),
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, ErrorCode.E429, "너무 많은 요청을 보냈습니다.", LogLevel.WARN),
    CONFLICT(HttpStatus.CONFLICT, ErrorCode.E409, "요청이 충돌했습니다. 다시 시도해주세요.", LogLevel.WARN),

    // Member
    INVALID_MEMBER_KEY(HttpStatus.BAD_REQUEST, ErrorCode.E1000, "멤버 key가 유효하지 않습니다.", LogLevel.WARN),

    // Security
    NOT_FOUND_SUBJECT(HttpStatus.BAD_REQUEST, ErrorCode.E2000, "Subject를 찾을 수 없습니다.", LogLevel.WARN),
    MALFORMED_JWT(HttpStatus.BAD_REQUEST, ErrorCode.E2001, "JWT가 손상되었습니다.", LogLevel.WARN),
    UNSUPPORTED_JWT(HttpStatus.BAD_REQUEST, ErrorCode.E2002, "지원하지 않는 JWT 형식입니다.", LogLevel.WARN),
    EXPIRED_JWT(HttpStatus.BAD_REQUEST, ErrorCode.E2003, "JWT 기한이 만료되었습니다.", LogLevel.WARN),
    INVALID_SIGNATURE(HttpStatus.BAD_REQUEST, ErrorCode.E2004, "JWT Signature 검증에 실패했습니다.", LogLevel.WARN),
    INVALID_JWT(HttpStatus.BAD_REQUEST, ErrorCode.E2005, "JWT가 유효하지 않습니다.", LogLevel.WARN),
    UNSUPPORTED_PROVIDER(HttpStatus.BAD_REQUEST, ErrorCode.E2006, "지원하지 않는 OAuth Provider 입니다.", LogLevel.WARN),
    INVALID_ONCE_AUTH_TOKEN(HttpStatus.BAD_REQUEST, ErrorCode.E2007, "유효하지 않거나 만료된 인증 토큰입니다.", LogLevel.WARN),
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
