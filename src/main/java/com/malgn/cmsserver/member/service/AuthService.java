package com.malgn.cmsserver.member.service;

import com.malgn.cmsserver.member.domain.Jwt;
import com.malgn.cmsserver.member.domain.RefreshToken;
import com.malgn.cmsserver.member.repository.RefreshTokenRepository;
import com.malgn.cmsserver.support.exception.AppException;
import com.malgn.cmsserver.support.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final OnceAuthTokenService onceAuthTokenService;
    private final JwtGenerator jwtGenerator;
    private final JwtValidator jwtValidator;

    public Jwt login(String onceAuthToken) {
        String memberKey = onceAuthTokenService.validateAndConsume(onceAuthToken);

        return jwtGenerator.generateJwt(memberKey);
    }

    public Jwt refresh(String refreshToken) {
        String memberKey = jwtValidator.getSubjectIfValid(refreshToken);

        RefreshToken savedRefreshToken = refreshTokenRepository.findByMemberMemberKey(memberKey)
                .orElseThrow(() -> new AppException(ErrorType.NOT_FOUND_DATA));

        if (!savedRefreshToken.isValid(refreshToken)) {
            throw new AppException(ErrorType.FAILED_AUTH);
        }

        Jwt jwt = jwtGenerator.generateJwt(memberKey);
        savedRefreshToken.update(jwt.refreshToken());
        return jwt;
    }
}
