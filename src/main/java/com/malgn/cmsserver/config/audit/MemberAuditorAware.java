package com.malgn.cmsserver.config.audit;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class MemberAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.empty();
    }
}
