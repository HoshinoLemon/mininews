package com.mininews.server.service;

import com.mininews.server.common.ContentType;
import com.mininews.server.common.Status;
import com.mininews.server.entity.Content;
import com.mininews.server.repository.ContentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContentService {

    private final ContentRepository contentRepository;

    public ContentService(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    public List<Content> latestPublished(ContentType type, int limit) {
        // 为了少文件，这里先用 Repository 的 top10 方法；limit 暂时不做动态化（后面再扩展分页）
        return contentRepository.findTop10ByTypeAndStatusAndDeletedOrderByPublishTimeDesc(
                type, Status.PUBLISHED, false
        );
    }
}
