package com.malgn.cmsserver.config.security.filter;

import com.malgn.cmsserver.config.security.handler.AuthenticationExceptionHandler;
import com.malgn.cmsserver.support.exception.AppException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthenticationExceptionTranslationFilter extends OncePerRequestFilter {

    private final AuthenticationExceptionHandler exceptionHandler;

    @NullMarked
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            doFilter(request, response, filterChain);
        } catch (AppException e) {
            exceptionHandler.handle(request, response, e);
        }
    }
}
