package com.malgn.cmsserver.config.security.oauth2.mapper;

import com.malgn.cmsserver.config.security.oauth2.OAuth2Request;
import com.malgn.cmsserver.member.domain.OAuthProvider;

import java.util.Map;

public interface AttributeMapper {

    OAuth2Request mapToRequest(Map<String, Object> attributes);

    OAuthProvider getProvider();
}
