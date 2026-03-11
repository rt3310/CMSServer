package com.malgn.cmsserver.contents.fixture;

import com.malgn.cmsserver.contents.domain.Contents;

public enum ContentsFixture {
    DEFAULT("테스트 제목", "테스트 설명"),
    ;

    private final String title;
    private final String description;

    ContentsFixture(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Contents toContents() {
        return Contents.builder()
                .title(title)
                .description(description)
                .build();
    }
}
