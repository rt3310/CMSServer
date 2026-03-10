package com.malgn.cmsserver.config.security.handler;

import com.malgn.cmsserver.member.domain.AuthMember;
import com.malgn.cmsserver.member.service.OnceAuthTokenService;
import com.malgn.cmsserver.support.exception.AppException;
import com.malgn.cmsserver.support.exception.ErrorType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final OnceAuthTokenService onceAuthTokenService;

    @Value("${client.url}")
    private String clientUrl;

    @Value("${client.endpoint}")
    private String endpoint;

    @NullMarked
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        AuthMember authMember = Optional.ofNullable((AuthMember) authentication.getPrincipal())
                .orElseThrow(() -> new AppException(ErrorType.SERVER_ERROR));

        getRedirectStrategy().sendRedirect(request, response,
                clientUrl + endpoint + "?token=" + onceAuthTokenService.generate(authMember.getName()));
    }
}
