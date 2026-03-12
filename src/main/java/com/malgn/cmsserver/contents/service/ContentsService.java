package com.malgn.cmsserver.contents.service;

import com.malgn.cmsserver.contents.domain.Contents;
import com.malgn.cmsserver.contents.repository.ContentsRepository;
import com.malgn.cmsserver.member.domain.Member;
import com.malgn.cmsserver.support.exception.AppException;
import com.malgn.cmsserver.support.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ContentsService {

    private final ContentsRepository contentsRepository;

    public Long create(String title, String description) {
        Contents contents = Contents.builder()
                .title(title)
                .description(description)
                .build();
        return contentsRepository.save(contents).getId();
    }

    @NullMarked
    @Transactional(readOnly = true)
    public Slice<Contents> getList(Pageable pageable) {
        return contentsRepository.findAllByOrderByCreatedDateDesc(pageable);
    }

    public Contents findDetailWithViewCount(Long id) {
        contentsRepository.increaseViewCount(id);
        return findById(id);
    }

    public Contents update(Long id, String title, String description, Member member) {
        Contents contents = findById(id);
        validateOwner(contents, member);
        contents.update(title, description);
        return contents;
    }

    public void delete(Long id, Member member) {
        Contents contents = findById(id);
        validateOwner(contents, member);
        contentsRepository.delete(contents);
    }

    private void validateOwner(Contents contents, Member member) {
        if (member.isAdmin()) {
            return;
        }
        if (!contents.isOwner(member.getMemberKey())) {
            throw new AppException(ErrorType.FORBIDDEN);
        }
    }

    private Contents findById(Long id) {
        return contentsRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorType.NOT_FOUND_DATA));
    }
}
