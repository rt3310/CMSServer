package com.malgn.cmsserver.contents.controller;

import com.malgn.cmsserver.config.security.annotation.AuthMember;
import com.malgn.cmsserver.contents.controller.dto.request.ContentsCreateRequest;
import com.malgn.cmsserver.contents.controller.dto.request.ContentsUpdateRequest;
import com.malgn.cmsserver.contents.controller.dto.response.ContentsListResponse;
import com.malgn.cmsserver.contents.controller.dto.response.ContentsResponse;
import com.malgn.cmsserver.contents.domain.Contents;
import com.malgn.cmsserver.contents.service.ContentsService;
import com.malgn.cmsserver.member.domain.Member;
import com.malgn.cmsserver.support.response.AppApiResponse;
import com.malgn.cmsserver.support.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Contents", description = "콘텐츠 API")
@NullMarked
@RestController
@RequestMapping("/api/v1/contents")
@RequiredArgsConstructor
public class ContentsController {

    private final ContentsService contentsService;

    @Operation(summary = "콘텐츠 생성", description = "새로운 콘텐츠를 생성합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PostMapping
    public ResponseEntity<AppApiResponse<ContentsResponse>> create(@Valid @RequestBody ContentsCreateRequest request) {
        Long contentsId = contentsService.create(request.title(), request.description());
        return ResponseEntity.created(URI.create("/api/v1/contents/" + contentsId)).build();
    }

    @Operation(summary = "콘텐츠 목록 조회", description = "콘텐츠 목록을 페이징하여 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @GetMapping
    public ResponseEntity<AppApiResponse<PageResponse<ContentsListResponse>>> getList(
            @Parameter(description = "페이지 정보 (page, size, sort)")
            @PageableDefault Pageable pageable) {
        Slice<ContentsListResponse> slice = contentsService.getList(pageable)
                .map(ContentsListResponse::from);
        return ResponseEntity.ok(AppApiResponse.success(PageResponse.from(slice)));
    }

    @Operation(summary = "콘텐츠 상세 조회", description = "콘텐츠 상세 정보를 조회합니다. 조회 시 조회수가 증가합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "콘텐츠를 찾을 수 없음"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AppApiResponse<ContentsResponse>> getDetail(
            @Parameter(description = "콘텐츠 ID", required = true)
            @PathVariable Long id) {
        Contents contents = contentsService.findDetailWithViewCount(id);
        return ResponseEntity.ok(AppApiResponse.success(ContentsResponse.from(contents)));
    }

    @Operation(summary = "콘텐츠 수정", description = "콘텐츠를 수정합니다. 작성자 또는 ADMIN만 수정 가능합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "콘텐츠를 찾을 수 없음"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AppApiResponse<ContentsResponse>> update(
            @Parameter(description = "콘텐츠 ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody ContentsUpdateRequest request,
            @Parameter(hidden = true) @AuthMember Member member) {
        Contents contents = contentsService.update(id, request.title(), request.description(), member);
        return ResponseEntity.ok(AppApiResponse.success(ContentsResponse.from(contents)));
    }

    @Operation(summary = "콘텐츠 삭제", description = "콘텐츠를 삭제합니다. 작성자 또는 ADMIN만 삭제 가능합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "400", description = "콘텐츠를 찾을 수 없음"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<AppApiResponse<Void>> delete(
            @Parameter(description = "콘텐츠 ID", required = true)
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthMember Member member) {
        contentsService.delete(id, member);
        return ResponseEntity.ok(AppApiResponse.success());
    }
}
