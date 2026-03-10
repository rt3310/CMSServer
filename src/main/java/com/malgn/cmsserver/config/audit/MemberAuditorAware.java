package com.malgn.cmsserver.config.audit;

import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@NullMarked
@Component
public class MemberAuditorAware implements AuditorAware<String> {

    private static final String ANONYMOUS_USER = "anonymousUser";
    private static final String SYSTEM = "SYSTEM";

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals(ANONYMOUS_USER)
                || authentication.getName() == null) {
            return Optional.of(SYSTEM);
        }

        return Optional.ofNullable(authentication.getName());
    }
}
