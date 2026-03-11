package com.malgn.cmsserver.member.repository;

import com.malgn.cmsserver.member.domain.RefreshToken;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@NullMarked
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByMemberMemberKey(String memberMemberKey);
}
