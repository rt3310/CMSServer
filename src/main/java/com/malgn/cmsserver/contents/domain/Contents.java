package com.malgn.cmsserver.contents.domain;

import com.malgn.cmsserver.common.domain.BaseEntity;
import jakarta.persistence.*;

@Entity
public class Contents extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Long viewCount;
}
