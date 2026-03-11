package com.malgn.cmsserver.contents.controller;

import com.malgn.cmsserver.config.security.annotation.AuthMember;
import com.malgn.cmsserver.contents.controller.dto.request.ContentsCreateRequest;
import com.malgn.cmsserver.contents.controller.dto.request.ContentsUpdateRequest;
import com.malgn.cmsserver.contents.controller.dto.response.ContentsListResponse;
import com.malgn.cmsserver.contents.controller.dto.response.ContentsResponse;
import com.malgn.cmsserver.contents.domain.Contents;
import com.malgn.cmsserver.contents.service.ContentsService;
import com.malgn.cmsserver.member.domain.Member;
import com.malgn.cmsserver.support.response.ApiResponse;
import com.malgn.cmsserver.support.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@NullMarked
@RestController
@RequestMapping("/api/v1/contents")
@RequiredArgsConstructor
public class ContentsController {

    private final ContentsService contentsService;

    @PostMapping
    public ResponseEntity<ApiResponse<ContentsResponse>> create(@Valid @RequestBody ContentsCreateRequest request) {
        Long contentsId = contentsService.create(request.title(), request.description());
        return ResponseEntity.created(URI.create("/api/v1/contents/" + contentsId)).build();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ContentsListResponse>>> getList(@PageableDefault Pageable pageable) {
        Slice<ContentsListResponse> slice = contentsService.getList(pageable)
                .map(ContentsListResponse::from);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(slice)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContentsResponse>> getDetail(@PathVariable Long id) {
        Contents contents = contentsService.getDetailWithViewCount(id);
        return ResponseEntity.ok(ApiResponse.success(ContentsResponse.from(contents)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ContentsResponse>> update(@PathVariable Long id,
                                                                @Valid @RequestBody ContentsUpdateRequest request,
                                                                @AuthMember Member member) {
        Contents contents = contentsService.update(id, request.title(), request.description(), member);
        return ResponseEntity.ok(ApiResponse.success(ContentsResponse.from(contents)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, @AuthMember Member member) {
        contentsService.delete(id, member);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
