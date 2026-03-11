package com.malgn.cmsserver.config.security.oauth2.mapper;

import com.malgn.cmsserver.config.security.oauth2.OAuth2Request;
import com.malgn.cmsserver.member.domain.OAuthProvider;
import com.malgn.cmsserver.support.exception.AppException;
import com.malgn.cmsserver.support.exception.ErrorType;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Component
public class KakaoAttributeMapper implements AttributeMapper {

    private static final String ID = "id";
    private static final String UNKNOWN = "Unknown";

    @Override
    public OAuth2Request mapToRequest(Map<String, Object> attributes) {
        String account = Optional.ofNullable(attributes.get(ID))
                .map(Object::toString)
                .orElseThrow(() -> new AppException(ErrorType.FAILED_AUTH));

        Map<String, Object> kakaoAccount = getAsMap(attributes, "kakao_account");
        Map<String, Object> profile = getAsMap(kakaoAccount, "profile");
        String name = profile.getOrDefault("nickname", UNKNOWN).toString();

        return new OAuth2Request(account, getProvider(), name);
    }

    private Map<String, Object> getAsMap(Map<String, Object> map, String key) {
        return (Map<String, Object>) map.getOrDefault(key, Collections.emptyMap());
    }

    @Override
    public OAuthProvider getProvider() {
        return OAuthProvider.KAKAO;
    }
}
