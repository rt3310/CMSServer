package com.malgn.cmsserver.config.security.oauth2;

import com.malgn.cmsserver.member.domain.OAuthProvider;

public record OAuth2Request(String account, OAuthProvider provider, String name) {
}
