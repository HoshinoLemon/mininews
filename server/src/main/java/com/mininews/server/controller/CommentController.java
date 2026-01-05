package com.mininews.server.controller;

import com.mininews.server.common.ApiResponse;
import com.mininews.server.common.AuthUser;
import com.mininews.server.common.ContentType;
import com.mininews.server.common.Status;
import com.mininews.server.config.AuthInterceptor;
import com.mininews.server.dto.CreateCommentRequest;
import com.mininews.server.dto.PageResult;
import com.mininews.server.entity.Comment;
import com.mininews.server.repository.ContentRepository;
import com.mininews.server.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class CommentController {

    private final CommentService commentService;
    private final ContentRepository contentRepository;

    public CommentController(CommentService commentService,
                             ContentRepository contentRepository) {
        this.commentService = commentService;
        this.contentRepository = contentRepository;
    }

    /**
     * GET /api/news/{newsId}/comments?page&size
     * 仅允许 newsId 对应内容为 NEWS 且 PUBLISHED 且未删除
     * 默认 page=1 size=10
     */
    @GetMapping("/api/news/{newsId}/comments")
    public ApiResponse<PageResult<CommentItem>> list(
            @PathVariable Long newsId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        ensurePublishedNewsExists(newsId);

        Page<Comment> p = commentService.pageComments(newsId, page, size);
        List<CommentItem> items = p.getContent().stream()
                .map(c -> new CommentItem(
                        c.getId(),
                        c.getNewsId(),
                        c.getUserId(),
                        c.getBody(),
                        c.getCreatedAt()
                ))
                .toList();

        PageResult<CommentItem> data = new PageResult<>(
                page,
                size,
                p.getTotalElements(),
                p.getTotalPages(),
                items
        );

        return ApiResponse.ok(data);
    }

    /**
     * POST /api/news/{newsId}/comments
     * 需要登录（由 AuthInterceptor 控制）
     * 仅允许对 NEWS 且 PUBLISHED 且未删除的新闻评论
     */
    @PostMapping("/api/news/{newsId}/comments")
    public ApiResponse<CommentItem> create(
            @PathVariable Long newsId,
            @Valid @RequestBody CreateCommentRequest req,
            HttpServletRequest request
    ) {
        ensurePublishedNewsExists(newsId);

        AuthUser user = (AuthUser) request.getAttribute(AuthInterceptor.ATTR_AUTH_USER);
        if (user == null) {
            // 理论上不会发生（拦截器已拦截），兜底
            throw new IllegalArgumentException("unauthorized");
        }

        Comment saved = commentService.addComment(newsId, user.id(), req.getBody());

        // createdAt 可能因为 insertable=false 而在对象里为 null，这里兜底给当前时间（数据库仍然会写 created_at）
        LocalDateTime createdAt = saved.getCreatedAt() != null ? saved.getCreatedAt() : LocalDateTime.now();

        CommentItem data = new CommentItem(
                saved.getId(),
                saved.getNewsId(),
                saved.getUserId(),
                saved.getBody(),
                createdAt
        );

        return ApiResponse.ok(data);
    }

    private void ensurePublishedNewsExists(Long newsId) {
        boolean ok = contentRepository.existsByIdAndTypeAndStatusAndDeletedFalse(newsId, ContentType.NEWS, Status.PUBLISHED);
        if (!ok) {
            throw new IllegalArgumentException("news not found");
        }
    }

    // 内部 DTO（减少文件数量）
    public record CommentItem(Long id, Long newsId, Long userId, String body, LocalDateTime createdAt) {}
}
