package com.mininews.server.controller;

import com.mininews.server.common.ApiResponse;
import com.mininews.server.common.AuthUser;
import com.mininews.server.common.ContentType;
import com.mininews.server.config.AuthInterceptor;
import com.mininews.server.dto.CreateCommentRequest;
import com.mininews.server.entity.Comment;
import com.mininews.server.repository.ContentRepository;
import com.mininews.server.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/api/news/{newsId}/comments")
    public ApiResponse<List<Comment>> list(@PathVariable Long newsId) {
        boolean isNews = contentRepository.existsByIdAndTypeAndDeletedFalse(newsId, ContentType.NEWS);
        if (!isNews) {
            throw new IllegalArgumentException("news not found");
        }
        return ApiResponse.ok(commentService.latestComments(newsId));
    }

    @PostMapping("/api/news/{newsId}/comments")
    public ApiResponse<Comment> create(@PathVariable Long newsId,
                                       @Valid @RequestBody CreateCommentRequest req,
                                       HttpServletRequest request) {
        boolean isNews = contentRepository.existsByIdAndTypeAndDeletedFalse(newsId, ContentType.NEWS);
        if (!isNews) {
            throw new IllegalArgumentException("news not found");
        }

        AuthUser user = (AuthUser) request.getAttribute(AuthInterceptor.ATTR_AUTH_USER);
        if (user == null) {
            // 理论上不会发生（拦截器已拦截），这里兜底
            throw new IllegalArgumentException("unauthorized");
        }

        Comment saved = commentService.addComment(newsId, user.id(), req.getBody());
        return ApiResponse.ok(saved);
    }
}
