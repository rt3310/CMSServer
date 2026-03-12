package com.malgn.cmsserver.config.security.filter;

import com.malgn.cmsserver.member.domain.Member;
import com.malgn.cmsserver.member.domain.TokenType;
import com.malgn.cmsserver.member.fixture.MemberFixture;
import com.malgn.cmsserver.member.service.JwtValidator;
import com.malgn.cmsserver.member.service.MemberService;
import com.malgn.cmsserver.support.exception.AppException;
import com.malgn.cmsserver.support.exception.ErrorType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    MemberService memberService;
    @Mock
    JwtValidator jwtValidator;
    @Mock
    FilterChain filterChain;
    @InjectMocks
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/v1/auth/login", "/api/v1/auth/refresh", "/api/v1/auth/logout"})
    @DisplayName("PERMIT_URLS에 포함된 경로는 필터링하지 않는다.")
    void shouldNotFilterPermitUrls(String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(uri);

        boolean result = jwtAuthenticationFilter.shouldNotFilter(request);

        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/v1/contents", "/api/v1/members", "/api/v2/auth/login"})
    @DisplayName("PERMIT_URLS에 포함되지 않은 경로는 필터링한다.")
    void shouldFilterNonPermitUrls(String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(uri);

        boolean result = jwtAuthenticationFilter.shouldNotFilter(request);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Authorization 헤더가 없으면 인증하지 않고 다음 필터로 넘긴다.")
    void doNotAuthenticateIfNoAuthorizationHeader() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI("/api/v1/contents");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("유효한 Bearer 토큰이면 SecurityContext에 인증 정보를 설정한다.")
    void authenticateWithValidBearerToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI("/api/v1/contents");
        String token = "Bearer validAccessToken";
        String memberKey = "memberKey";
        request.addHeader("Authorization", token);

        Member member = MemberFixture.DEFAULT.toMember();

        given(jwtValidator.getSubjectIfValidWithType(token, TokenType.ACCESS)).willReturn(memberKey);
        given(memberService.find(memberKey)).willReturn(member);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().isAuthenticated()).isTrue();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Bearer prefix가 없는 토큰이면 AppException이 발생한다.")
    void throwAppExceptionIfNotBearerToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI("/api/v1/contents");
        String token = "invalidTokenWithoutBearer";
        request.addHeader("Authorization", token);

        given(jwtValidator.getSubjectIfValidWithType(token, TokenType.ACCESS))
                .willThrow(new AppException(ErrorType.INVALID_TOKEN_METHOD));

        assertThatThrownBy(() -> jwtAuthenticationFilter.doFilterInternal(request, response, filterChain))
                .isInstanceOf(AppException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.INVALID_TOKEN_METHOD);
    }

    @Test
    @DisplayName("Basic 인증 방식이면 AppException이 발생한다.")
    void throwAppExceptionIfBasicAuthToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI("/api/v1/contents");
        String token = "Basic dXNlcm5hbWU6cGFzc3dvcmQ=";
        request.addHeader("Authorization", token);

        given(jwtValidator.getSubjectIfValidWithType(token, TokenType.ACCESS))
                .willThrow(new AppException(ErrorType.INVALID_TOKEN_METHOD));

        assertThatThrownBy(() -> jwtAuthenticationFilter.doFilterInternal(request, response, filterChain))
                .isInstanceOf(AppException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.INVALID_TOKEN_METHOD);
    }
}
