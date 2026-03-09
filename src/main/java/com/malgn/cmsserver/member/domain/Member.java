package com.malgn.cmsserver.member.domain;

import com.malgn.cmsserver.global.domain.BaseEntity;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String oAuthProvider;

    @Column(nullable = false)
    private String oAuthAccount;

    @Column(nullable = false)
    private String name;

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    private List<MemberRole> roles;
}
