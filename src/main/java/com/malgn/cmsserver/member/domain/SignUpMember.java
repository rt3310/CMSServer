package com.malgn.cmsserver.member.domain;

import com.malgn.cmsserver.config.security.oauth2.OAuth2Request;

import java.util.List;

public record SignUpMember(
        OAuthProvider oAuthProvider,
        String oauthAccount,
        String name
) {

    public Member toMember() {
        return Member.builder()
                .oauthProvider(oAuthProvider)
                .oauthAccount(oauthAccount)
                .name(name)
                .roles(List.of(MemberRole.ROLE_USER))
                .build();
    }

    public static SignUpMember of(OAuth2Request oAuth2Request) {
        return new SignUpMember(oAuth2Request.provider(), oAuth2Request.account(), oAuth2Request.name());
    }
}
