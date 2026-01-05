package com.mininews.server.repository;

import com.mininews.server.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findTop20ByNewsIdAndDeletedOrderByCreatedAtDesc(Long newsId, Boolean deleted);

    long countByNewsIdAndDeleted(Long newsId, Boolean deleted);
}
