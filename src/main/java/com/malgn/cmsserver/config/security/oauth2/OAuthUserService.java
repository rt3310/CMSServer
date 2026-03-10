package com.malgn.cmsserver.config.security.oauth2;

import com.malgn.cmsserver.config.security.oauth2.mapper.AttributeMapperFactory;
import com.malgn.cmsserver.member.domain.AuthMember;
import com.malgn.cmsserver.member.domain.Member;
import com.malgn.cmsserver.member.domain.OAuthProvider;
import com.malgn.cmsserver.member.domain.SignUpMember;
import com.malgn.cmsserver.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class OAuthUserService extends DefaultOAuth2UserService {

    private final AttributeMapperFactory attributeMapperFactory;
    private final MemberService memberService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuthProvider provider = OAuthProvider.valueOf(userRequest.getClientRegistration().getClientName().toUpperCase());
        OAuth2Request oAuth2Request = attributeMapperFactory.getMapper(provider).mapToRequest(oAuth2User.getAttributes());

        Member member = memberService.findByProvider(oAuth2Request.provider(), oAuth2Request.account())
                .orElseGet(() -> memberService.signUp(SignUpMember.of(oAuth2Request)));

        return toAuthMember(member);
    }

    private AuthMember toAuthMember(Member member) {
        return new AuthMember(member, new HashMap<>(), member.getAuthorities());
    }
}
