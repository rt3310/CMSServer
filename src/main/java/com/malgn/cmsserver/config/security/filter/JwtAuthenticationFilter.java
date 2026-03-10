package com.malgn.cmsserver.config.security.filter;

import com.malgn.cmsserver.member.domain.AuthMember;
import com.malgn.cmsserver.member.domain.Member;
import com.malgn.cmsserver.member.service.JwtValidator;
import com.malgn.cmsserver.member.service.MemberService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER = "Bearer ";

    private final MemberService memberService;
    private final JwtValidator jwtValidator;

    @NullMarked
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        getTokenFromHeader(request).ifPresent(this::authenticate);

        doFilter(request, response, filterChain);
    }

    private Optional<String> getTokenFromHeader(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION));
    }

    private void authenticate(String token) {
        if (!isBearerToken(token)) {
            return;
        }

        String subject = jwtValidator.getSubjectIfValid(token.substring(BEARER.length()));
        Member member = memberService.find(subject);
        List<SimpleGrantedAuthority> authorities = member.getAuthorities();
        AuthMember authMember = new AuthMember(member, new HashMap<>(), authorities);

        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(authMember, null, authorities));
    }

    private boolean isBearerToken(String token) {
        return token.startsWith(BEARER);
    }
}
