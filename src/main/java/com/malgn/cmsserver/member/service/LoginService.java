package com.malgn.cmsserver.member.service;

import com.malgn.cmsserver.member.domain.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final JwtValidator jwtValidator;
    private final JwtGenerator jwtGenerator;

    public Jwt login(String onceAuthToken) {
        String subject = jwtValidator.getSubjectIfValid(onceAuthToken);

        return jwtGenerator.generateJwt(subject);
    }
}
