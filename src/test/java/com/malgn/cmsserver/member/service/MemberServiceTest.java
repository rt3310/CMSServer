package com.malgn.cmsserver.member.service;

import com.malgn.cmsserver.member.domain.Member;
import com.malgn.cmsserver.member.domain.MemberRole;
import com.malgn.cmsserver.member.domain.OAuthProvider;
import com.malgn.cmsserver.member.domain.SignUpMember;
import com.malgn.cmsserver.member.fixture.MemberFixture;
import com.malgn.cmsserver.member.repository.MemberRepository;
import com.malgn.cmsserver.support.exception.AppException;
import com.malgn.cmsserver.support.exception.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;
    @InjectMocks
    MemberService memberService;

    @Test
    @DisplayName("memberKey로 회원을 조회한다.")
    void findByMemberKey() {
        String memberKey = "memberKey";
        Member member = MemberFixture.DEFAULT.toMember();

        given(memberRepository.findByMemberKey(memberKey)).willReturn(Optional.of(member));

        Member foundMember = memberService.find(memberKey);

        assertThat(foundMember.getName()).isEqualTo("name");
    }

    @Test
    @DisplayName("memberKey로 회원을 조회할 때 회원이 없으면 AppException이 발생한다.")
    void throwAppExceptionIfMemberNotFound() {
        String memberKey = "notExistsMemberKey";

        given(memberRepository.findByMemberKey(memberKey)).willReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.find(memberKey))
                .isInstanceOf(AppException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.NOT_FOUND_DATA);
    }

    @Test
    @DisplayName("provider와 account로 회원을 조회한다.")
    void findByProviderAndAccount() {
        OAuthProvider provider = OAuthProvider.KAKAO;
        String account = "kakaoAccount";
        Member member = MemberFixture.DEFAULT.toMember();

        given(memberRepository.findByOauthProviderAndOauthAccount(provider, account))
                .willReturn(Optional.of(member));

        Optional<Member> foundMember = memberService.findByProvider(provider, account);

        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getName()).isEqualTo("name");
    }

    @Test
    @DisplayName("provider와 account로 회원을 조회할 때 회원이 없으면 빈 Optional을 반환한다.")
    void returnEmptyOptionalIfMemberNotFoundByProvider() {
        OAuthProvider provider = OAuthProvider.KAKAO;
        String account = "notExistsAccount";

        given(memberRepository.findByOauthProviderAndOauthAccount(provider, account))
                .willReturn(Optional.empty());

        Optional<Member> foundMember = memberService.findByProvider(provider, account);

        assertThat(foundMember).isEmpty();
    }

    @Test
    @DisplayName("회원가입에 성공하면 저장된 회원을 반환한다.")
    void signUp() {
        SignUpMember signUpMember = new SignUpMember(OAuthProvider.KAKAO, "kakaoAccount", "name");
        Member member = MemberFixture.DEFAULT.toMember();

        given(memberRepository.save(any(Member.class))).willReturn(member);

        Member savedMember = memberService.signUp(signUpMember);

        assertThat(savedMember.getName()).isEqualTo("name");
        assertThat(savedMember.getOauthProvider()).isEqualTo(OAuthProvider.KAKAO);
    }
}
