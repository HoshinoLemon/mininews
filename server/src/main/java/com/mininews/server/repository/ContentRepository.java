package com.mininews.server.repository;

import com.mininews.server.common.ContentType;
import com.mininews.server.common.Status;
import com.mininews.server.entity.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContentRepository extends JpaRepository<Content, Long> {

    List<Content> findTop10ByTypeAndStatusAndDeletedOrderByPublishTimeDesc(
            ContentType type,
            Status status,
            Boolean deleted
    );

    Optional<Content> findByIdAndDeletedFalse(Long id);

    boolean existsByIdAndTypeAndDeletedFalse(Long id, ContentType type);

    Page<Content> findByTypeAndStatusAndDeletedFalseOrderByPublishTimeDesc(
            ContentType type,
            Status status,
            Pageable pageable
    );

    Optional<Content> findByIdAndStatusAndDeletedFalse(Long id, Status status);

    // 新增：用于评论校验（仅允许对已发布新闻评论）
    boolean existsByIdAndTypeAndStatusAndDeletedFalse(Long id, ContentType type, Status status);
}
