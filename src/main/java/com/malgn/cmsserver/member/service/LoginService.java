package com.malgn.cmsserver.member.service;

import com.malgn.cmsserver.member.domain.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final OnceAuthTokenService onceAuthTokenService;
    private final JwtGenerator jwtGenerator;

    public Jwt login(String onceAuthToken) {
        String memberKey = onceAuthTokenService.validateAndConsume(onceAuthToken);

        return jwtGenerator.generateJwt(memberKey);
    }
}
