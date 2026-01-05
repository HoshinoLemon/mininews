package com.mininews.server.service;

import com.mininews.server.entity.Comment;
import com.mininews.server.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> latestComments(Long newsId) {
        return commentRepository.findTop20ByNewsIdAndDeletedOrderByCreatedAtDesc(newsId, false);
    }

    public Comment addComment(Long newsId, Long userId, String body) {
        Comment c = new Comment();
        c.setNewsId(newsId);
        c.setUserId(userId);
        c.setBody(body);
        c.setDeleted(false);
        return commentRepository.save(c);
    }
}
