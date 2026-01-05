package com.mininews.server.service;

import com.mininews.server.common.ContentType;
import com.mininews.server.common.Status;
import com.mininews.server.entity.Content;
import com.mininews.server.repository.ContentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ContentService {

    private final ContentRepository contentRepository;

    public ContentService(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    /**
     * 用户端分页列表：只返回 PUBLISHED 且 deleted=false
     * pageFrom1: 页码从 1 开始
     */
    public Page<Content> pagePublished(ContentType type, int pageFrom1, int size) {
        if (pageFrom1 < 1) {
            pageFrom1 = 1;
        }
        if (size < 1 || size > 50) {
            throw new IllegalArgumentException("size must be between 1 and 50");
        }

        PageRequest pageable = PageRequest.of(
                pageFrom1 - 1,
                size,
                Sort.by(Sort.Direction.DESC, "publishTime")
        );

        return contentRepository.findByTypeAndStatusAndDeletedFalseOrderByPublishTimeDesc(
                type,
                Status.PUBLISHED,
                pageable
        );
    }

    /**
     * 用户端详情：只允许访问 PUBLISHED 且 deleted=false
     */
    public Content getPublishedById(Long id) {
        return contentRepository.findByIdAndStatusAndDeletedFalse(id, Status.PUBLISHED)
                .orElseThrow(() -> new IllegalArgumentException("content not found"));
    }
}
