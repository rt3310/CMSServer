package com.malgn.cmsserver.config.audit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class MemberAuditorAwareTest {

    MemberAuditorAware memberAuditorAware = new MemberAuditorAware();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("인증 정보가 있으면 사용자 이름을 반환한다.")
    void returnUsernameIfAuthenticated() {
        String memberKey = "memberKey";
        setAuthentication(memberKey);

        Optional<String> auditor = memberAuditorAware.getCurrentAuditor();

        assertThat(auditor).isPresent();
        assertThat(auditor.get()).isEqualTo(memberKey);
    }

    @Test
    @DisplayName("authentication이 null이면 SYSTEM을 반환한다.")
    void returnSystemIfAuthenticationIsNull() {
        Optional<String> auditor = memberAuditorAware.getCurrentAuditor();

        assertThat(auditor).isPresent();
        assertThat(auditor.get()).isEqualTo("SYSTEM");
    }

    @Test
    @DisplayName("인증되지 않은 상태면 SYSTEM을 반환한다.")
    void returnSystemIfNotAuthenticated() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("user", "password", Collections.emptyList());
        authentication.setAuthenticated(false);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Optional<String> auditor = memberAuditorAware.getCurrentAuditor();

        assertThat(auditor).isPresent();
        assertThat(auditor.get()).isEqualTo("SYSTEM");
    }

    @Test
    @DisplayName("principal이 anonymousUser면 SYSTEM을 반환한다.")
    void returnSystemIfAnonymousUser() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("anonymousUser", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Optional<String> auditor = memberAuditorAware.getCurrentAuditor();

        assertThat(auditor).isPresent();
        assertThat(auditor.get()).isEqualTo("SYSTEM");
    }

    private void setAuthentication(String memberKey) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(memberKey, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
