-- =========================================================
-- MiniNews init.sql
-- 功能：创建数据库 + 创建表(user, content) + 插入默认管理员
-- 适用：MySQL 8.x
-- =========================================================

-- 1) 创建数据库
-- 强制客户端/连接使用 utf8mb4，避免中文导入乱码报错
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET collation_connection = 'utf8mb4_0900_ai_ci';

-- =========================================================
-- MiniNews init.sql (with comments + rich text HTML body)
-- 功能：创建数据库 + 创建表(user, content, comment) + 初始化数据
-- 适用：MySQL 8.x
-- =========================================================

CREATE DATABASE IF NOT EXISTS mininews
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE mininews;

-- 为脚本可重复执行：先删表（注意顺序）
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS content;
DROP TABLE IF EXISTS user;

-- 用户表：user
CREATE TABLE user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(32) NOT NULL UNIQUE,
  password_hash VARCHAR(100) NOT NULL,
  role VARCHAR(10) NOT NULL DEFAULT 'USER',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 内容表：content（新闻/公告共用）
-- body: 存 HTML 字符串（支持 <p> 与 <img> 排版）
CREATE TABLE content (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  type VARCHAR(10) NOT NULL,          -- NEWS / NOTICE
  title VARCHAR(100) NOT NULL,
  body TEXT NOT NULL,                 -- HTML
  status VARCHAR(12) NOT NULL DEFAULT 'DRAFT', -- DRAFT / PUBLISHED / OFFLINE
  author_id BIGINT NULL,
  publish_time DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT(1) NOT NULL DEFAULT 0,
  INDEX idx_type_status_time (type, status, publish_time),
  INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 评论表：comment（仅用于新闻 NEWS）
CREATE TABLE comment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  news_id BIGINT NOT NULL,            -- 对应 content.id（仅当 content.type=NEWS）
  user_id BIGINT NOT NULL,            -- 对应 user.id
  body TEXT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted TINYINT(1) NOT NULL DEFAULT 0,
  INDEX idx_news_time (news_id, created_at),
  INDEX idx_user_time (user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 默认管理员
INSERT INTO user (username, password_hash, role)
VALUES (
  'admin',
  '$2a$10$9zZrT8Y9o2rQpU8vF6g7yOq6dJw8l0e1mQ1uP0j6oBqGm0YqvQb2m',
  'ADMIN'
);

-- 演示内容（body 为 HTML）
INSERT INTO content (type, title, body, status, author_id, publish_time, deleted)
VALUES
('NOTICE', '系统公告：欢迎使用 MiniNews',
 '<p>这是一个演示公告。</p><p>公告不需要评论功能。</p>',
 'PUBLISHED', 1, NOW(), 0),

('NEWS', '新闻示例：项目启动',
 '<p>这是第一段新闻文字。</p><p>这里可以插入图片：</p><p><img src="/uploads/demo.jpg" /></p><p>这是图片后的文字。</p>',
 'PUBLISHED', 1, NOW(), 0),

('NEWS', '草稿示例：未发布新闻',
 '<p>这是一条草稿新闻，用户端不应看到。</p>',
 'DRAFT', 1, NULL, 0);

-- 可选：演示评论（news_id=2 对应上面的“新闻示例：项目启动”）
INSERT INTO comment (news_id, user_id, body, deleted)
VALUES
(2, 1, '这是一条演示评论（管理员也可以评论）。', 0);
