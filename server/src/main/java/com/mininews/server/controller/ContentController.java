package com.mininews.server.controller;

import com.mininews.server.common.ApiResponse;
import com.mininews.server.common.ContentType;
import com.mininews.server.entity.Content;
import com.mininews.server.dto.PageResult;
import com.mininews.server.service.ContentService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ContentController {

    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    /**
     * GET /api/contents?type=NEWS|NOTICE&page&size
     * 仅返回 PUBLISHED 且 deleted=false
     *
     * page 从 1 开始；size 建议 1~50
     */
    @GetMapping("/contents")
    public ApiResponse<PageResult<ContentListItem>> list(
            @RequestParam ContentType type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Content> result = contentService.pagePublished(type, page, size);

        List<ContentListItem> items = result.getContent().stream()
                .map(c -> new ContentListItem(
                        c.getId(),
                        c.getType().name(),
                        c.getTitle(),
                        c.getPublishTime()
                ))
                .toList();

        PageResult<ContentListItem> data = new PageResult<>(
                page,
                size,
                result.getTotalElements(),
                result.getTotalPages(),
                items
        );

        return ApiResponse.ok(data);
    }

    /**
     * GET /api/contents/{id}
     * 仅允许访问 PUBLISHED 且 deleted=false
     * 返回 body（HTML 字符串）
     */
    @GetMapping("/contents/{id}")
    public ApiResponse<ContentDetail> detail(@PathVariable Long id) {
        Content c = contentService.getPublishedById(id);

        ContentDetail data = new ContentDetail(
                c.getId(),
                c.getType().name(),
                c.getTitle(),
                c.getBody(),          // HTML 字符串
                c.getPublishTime()
        );

        return ApiResponse.ok(data);
    }

    // ===== 内部 DTO（减少文件数量）=====
    public record ContentListItem(Long id, String type, String title, LocalDateTime publishTime) {}
    public record ContentDetail(Long id, String type, String title, String body, LocalDateTime publishTime) {}
}
