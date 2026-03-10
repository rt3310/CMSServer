package com.malgn.cmsserver.member.fixture;

import com.malgn.cmsserver.member.domain.Member;
import com.malgn.cmsserver.member.domain.MemberRole;
import com.malgn.cmsserver.member.domain.OAuthProvider;

import java.util.List;

public enum MemberFixture {
    DEFAULT(OAuthProvider.KAKAO, "kakaoAccount", "name", List.of(MemberRole.ROLE_USER)),
    ;

    private final OAuthProvider oAuthProvider;
    private final String oauthAccount;
    private final String name;
    private final List<MemberRole> roles;

    MemberFixture(OAuthProvider oAuthProvider, String oauthAccount, String name, List<MemberRole> roles) {
        this.oAuthProvider = oAuthProvider;
        this.oauthAccount = oauthAccount;
        this.name = name;
        this.roles = roles;
    }

    public Member toMember() {
        return Member.builder()
                .oauthProvider(oAuthProvider)
                .oauthAccount(oauthAccount)
                .name(name)
                .roles(roles)
                .build();
    }
}
