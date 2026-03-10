package com.malgn.cmsserver.member.service;

import com.malgn.cmsserver.member.domain.Member;
import com.malgn.cmsserver.member.domain.OAuthProvider;
import com.malgn.cmsserver.member.domain.SignUpMember;
import com.malgn.cmsserver.member.repository.MemberRepository;
import com.malgn.cmsserver.support.exception.AppException;
import com.malgn.cmsserver.support.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Member find(String memberKey) {
        return memberRepository.findByMemberKey(memberKey)
                .orElseThrow(() -> new AppException(ErrorType.NOT_FOUND_DATA));
    }

    @Transactional(readOnly = true)
    public Optional<Member> findByProvider(OAuthProvider oauthProvider, String oAuthAccount) {
        return memberRepository.findByOauthProviderAndOauthAccount(oauthProvider, oAuthAccount);
    }

    public Member signUp(SignUpMember signUpMember) {
        return memberRepository.save(signUpMember.toMember());
    }
}
