package com.malgn.cmsserver.contents.service;

import com.malgn.cmsserver.contents.domain.Contents;
import com.malgn.cmsserver.contents.fixture.ContentsFixture;
import com.malgn.cmsserver.contents.repository.ContentsRepository;
import com.malgn.cmsserver.member.domain.Member;
import com.malgn.cmsserver.member.fixture.MemberFixture;
import com.malgn.cmsserver.support.exception.AppException;
import com.malgn.cmsserver.support.exception.ErrorType;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ContentsServiceTest {

    @Mock
    ContentsRepository contentsRepository;
    @InjectMocks
    ContentsService contentsService;

    @Test
    @DisplayName("콘텐츠를 생성하고 ID를 반환한다.")
    void createContents() {
        Contents contents = ContentsFixture.DEFAULT.toContents();
        ReflectionTestUtils.setField(contents, "id", 1L);

        given(contentsRepository.save(any(Contents.class))).willReturn(contents);

        Long contentsId = contentsService.create("제목", "설명");

        assertThat(contentsId).isEqualTo(1L);
        verify(contentsRepository).save(any(Contents.class));
    }

    @Test
    @DisplayName("콘텐츠 목록을 페이징하여 반환한다.")
    void getContentsList() {
        Pageable pageable = PageRequest.of(0, 10);
        Contents contents = ContentsFixture.DEFAULT.toContents();
        SliceImpl<@NonNull Contents> slice = new SliceImpl<>(List.of(contents), pageable, false);

        given(contentsRepository.findAllByOrderByCreatedDateDesc(pageable)).willReturn(slice);

        var result = contentsService.getList(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.hasNext()).isFalse();
    }

    @Test
    @DisplayName("조회수를 증가시키고 콘텐츠를 반환한다.")
    void getDetailAndIncreaseViewCount() {
        Long contentsId = 1L;
        Contents contents = ContentsFixture.DEFAULT.toContents();

        given(contentsRepository.findById(contentsId)).willReturn(Optional.of(contents));

        Contents result = contentsService.getDetailWithViewCount(contentsId);

        assertThat(result.getTitle()).isEqualTo("테스트 제목");
        verify(contentsRepository).increaseViewCount(contentsId);
    }

    @Test
    @DisplayName("콘텐츠가 존재하지 않으면 예외가 발생한다.")
    void throwExceptionWhenContentsNotFound() {
        Long contentsId = 999L;

        given(contentsRepository.findById(contentsId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> contentsService.getDetailWithViewCount(contentsId))
                .isInstanceOf(AppException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.NOT_FOUND_DATA);
    }

    @Test
    @DisplayName("소유자가 콘텐츠를 수정할 수 있다.")
    void ownerCanUpdateContents() {
        Long contentsId = 1L;
        Member owner = MemberFixture.DEFAULT.toMember();
        Contents contents = ContentsFixture.DEFAULT.toContents();
        ReflectionTestUtils.setField(contents, "createdBy", owner.getMemberKey());

        given(contentsRepository.findById(contentsId)).willReturn(Optional.of(contents));

        Contents result = contentsService.update(contentsId, "수정된 제목", "수정된 설명", owner);

        assertThat(result.getTitle()).isEqualTo("수정된 제목");
        assertThat(result.getDescription()).isEqualTo("수정된 설명");
    }

    @Test
    @DisplayName("Admin은 모든 콘텐츠를 수정할 수 있다.")
    void adminCanUpdateAnyContents() {
        Long contentsId = 1L;
        Member admin = MemberFixture.ADMIN.toMember();
        Member otherUser = MemberFixture.DEFAULT.toMember();
        Contents contents = ContentsFixture.DEFAULT.toContents();
        ReflectionTestUtils.setField(contents, "createdBy", otherUser.getMemberKey());

        given(contentsRepository.findById(contentsId)).willReturn(Optional.of(contents));

        Contents result = contentsService.update(contentsId, "수정된 제목", "수정된 설명", admin);

        assertThat(result.getTitle()).isEqualTo("수정된 제목");
    }

    @Test
    @DisplayName("소유자가 아니면 콘텐츠를 수정할 수 없다.")
    void nonOwnerCannotUpdateContents() {
        Long contentsId = 1L;
        Member owner = MemberFixture.DEFAULT.toMember();
        Member otherUser = MemberFixture.DEFAULT.toMember();
        Contents contents = ContentsFixture.DEFAULT.toContents();
        ReflectionTestUtils.setField(contents, "createdBy", owner.getMemberKey());

        given(contentsRepository.findById(contentsId)).willReturn(Optional.of(contents));

        assertThatThrownBy(() -> contentsService.update(contentsId, "수정된 제목", "수정된 설명", otherUser))
                .isInstanceOf(AppException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.FORBIDDEN);
    }

    @Test
    @DisplayName("소유자가 콘텐츠를 삭제할 수 있다.")
    void ownerCanDeleteContents() {
        Long contentsId = 1L;
        Member owner = MemberFixture.DEFAULT.toMember();
        Contents contents = ContentsFixture.DEFAULT.toContents();
        ReflectionTestUtils.setField(contents, "createdBy", owner.getMemberKey());

        given(contentsRepository.findById(contentsId)).willReturn(Optional.of(contents));

        contentsService.delete(contentsId, owner);

        verify(contentsRepository).delete(contents);
    }

    @Test
    @DisplayName("Admin은 모든 콘텐츠를 삭제할 수 있다.")
    void adminCanDeleteAnyContents() {
        Long contentsId = 1L;
        Member admin = MemberFixture.ADMIN.toMember();
        Member otherUser = MemberFixture.DEFAULT.toMember();
        Contents contents = ContentsFixture.DEFAULT.toContents();
        ReflectionTestUtils.setField(contents, "createdBy", otherUser.getMemberKey());

        given(contentsRepository.findById(contentsId)).willReturn(Optional.of(contents));

        contentsService.delete(contentsId, admin);

        verify(contentsRepository).delete(contents);
    }

    @Test
    @DisplayName("소유자가 아니면 콘텐츠를 삭제할 수 없다.")
    void nonOwnerCannotDeleteContents() {
        Long contentsId = 1L;
        Member owner = MemberFixture.DEFAULT.toMember();
        Member otherUser = MemberFixture.DEFAULT.toMember();
        Contents contents = ContentsFixture.DEFAULT.toContents();
        ReflectionTestUtils.setField(contents, "createdBy", owner.getMemberKey());

        given(contentsRepository.findById(contentsId)).willReturn(Optional.of(contents));

        assertThatThrownBy(() -> contentsService.delete(contentsId, otherUser))
                .isInstanceOf(AppException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.FORBIDDEN);
    }
}
