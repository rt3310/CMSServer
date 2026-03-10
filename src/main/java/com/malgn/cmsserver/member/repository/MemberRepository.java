package com.malgn.cmsserver.member.repository;

import com.malgn.cmsserver.member.domain.Member;
import com.malgn.cmsserver.member.domain.OAuthProvider;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@NullMarked
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMemberKey(String memberKey);

    Optional<Member> findByOauthProviderAndOauthAccount(OAuthProvider oauthProvider, String oauthAccount);
}
