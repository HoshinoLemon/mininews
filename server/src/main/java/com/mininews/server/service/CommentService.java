package com.mininews.server.service;

import com.mininews.server.entity.Comment;
import com.mininews.server.repository.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    /**
     * 分页获取评论（page 从 1 开始）
     */
    public Page<Comment> pageComments(Long newsId, int pageFrom1, int size) {
        if (pageFrom1 < 1) {
            pageFrom1 = 1;
        }
        if (size < 1 || size > 50) {
            throw new IllegalArgumentException("size must be between 1 and 50");
        }

        PageRequest pageable = PageRequest.of(
                pageFrom1 - 1,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return commentRepository.findByNewsIdAndDeletedFalseOrderByCreatedAtDesc(newsId, pageable);
    }

    /**
     * 新增评论（created_at 由数据库默认值写入）
     */
    public Comment addComment(Long newsId, Long userId, String body) {
        Comment c = new Comment();
        c.setNewsId(newsId);
        c.setUserId(userId);
        c.setBody(body);
        c.setDeleted(false);
        return commentRepository.save(c);
    }
}
