package com.malgn.cmsserver.member.domain;

import com.malgn.cmsserver.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String memberKey;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OAuthProvider oauthProvider;

    @Column(nullable = false)
    private String oauthAccount;

    @Column(nullable = false)
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<MemberRole> roles;

    @Builder
    public Member(OAuthProvider oauthProvider, String oauthAccount, String name, List<MemberRole> roles) {
        this.memberKey = UUID.randomUUID().toString();
        this.oauthProvider = oauthProvider;
        this.oauthAccount = oauthAccount;
        this.name = name;
        this.roles = roles;
    }

    public List<SimpleGrantedAuthority> getAuthorities() {
        return roles.stream().map(MemberRole::name).map(SimpleGrantedAuthority::new).toList();
    }

    public boolean isAdmin() {
        return roles.contains(MemberRole.ROLE_ADMIN);
    }
}
