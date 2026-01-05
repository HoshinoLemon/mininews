package com.mininews.server.repository;

import com.mininews.server.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 分页获取评论（只取未删除），按创建时间倒序
    Page<Comment> findByNewsIdAndDeletedFalseOrderByCreatedAtDesc(Long newsId, Pageable pageable);
}
