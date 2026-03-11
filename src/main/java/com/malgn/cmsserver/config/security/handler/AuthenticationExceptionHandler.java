package com.malgn.cmsserver.config.security.handler;

import com.malgn.cmsserver.common.util.NetworkUtils;
import com.malgn.cmsserver.support.exception.AppException;
import com.malgn.cmsserver.support.exception.ErrorType;
import com.malgn.cmsserver.support.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationExceptionHandler {

    private static final String USER_AGENT = "User-Agent";

    private final ObjectMapper objectMapper;

    public void handle(HttpServletRequest request, HttpServletResponse response, AppException exception)
            throws IOException {
        if (response.isCommitted()) {
            return;
        }
        writeUnauthorizedResponse(request, response, ApiResponse.error(exception.getErrorType()));
    }

    public void handle(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException {
        if (response.isCommitted()) {
            return;
        }
        writeUnauthorizedResponse(request, response, ApiResponse.error(resolveErrorCode(exception)));
    }

    private void writeUnauthorizedResponse(HttpServletRequest request, HttpServletResponse response,
                                           ApiResponse<Void> body)
            throws IOException {
        String clientIp = NetworkUtils.getClientIp(request);
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String userAgent = request.getHeader(USER_AGENT);
        String errorCode = body.error().errorCode();
        String message = body.error().message();
        log.warn("[Authentication Exception]: IP={} | Method={} | URI={} | UserAgent={} | errorCode={} | message={}",
                clientIp, method, uri, userAgent, errorCode, message);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(body)));
        writer.flush();
    }

    private ErrorType resolveErrorCode(AuthenticationException e) {
        if (e instanceof AuthenticationCredentialsNotFoundException
                || e instanceof InsufficientAuthenticationException) {
            return ErrorType.REQUIRED_AUTH;
        } else {
            return ErrorType.FAILED_AUTH;
        }
    }
}
