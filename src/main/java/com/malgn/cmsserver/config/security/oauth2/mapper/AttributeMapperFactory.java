package com.malgn.cmsserver.config.security.oauth2.mapper;

import com.malgn.cmsserver.member.domain.OAuthProvider;
import com.malgn.cmsserver.support.exception.AppException;
import com.malgn.cmsserver.support.exception.ErrorType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class AttributeMapperFactory {

    private final Map<OAuthProvider, AttributeMapper> mapperMap = new EnumMap<>(OAuthProvider.class);

    public AttributeMapperFactory(List<AttributeMapper> mappers) {
        for (AttributeMapper mapper : mappers) {
            mapperMap.put(mapper.getProvider(), mapper);
        }
    }

    public AttributeMapper getMapper(OAuthProvider oAuthProvider) {
        return Optional.ofNullable(mapperMap.get(oAuthProvider))
                .orElseThrow(() -> new AppException(ErrorType.UNSUPPORTED_PROVIDER));
    }
}
