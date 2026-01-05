package com.mininews.server.entity;

import com.mininews.server.common.ContentType;
import com.mininews.server.common.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "content")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * NEWS / NOTICE
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private ContentType type;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    /**
     * 富文本内容：存 HTML 字符串（可包含 <p>、<img> 等）
     */
    @Lob
    @Column(name = "body", nullable = false)
    private String body;

    /**
     * DRAFT / PUBLISHED / OFFLINE
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 12)
    private Status status;

    @Column(name = "author_id")
    private Long authorId;

    @Column(name = "publish_time")
    private LocalDateTime publishTime;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (deleted == null) {
            deleted = false;
        }
        if (status == null) {
            status = Status.DRAFT;
        }
    }
}
