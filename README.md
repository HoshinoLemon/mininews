# MiniNews（迷你新闻公告系统）

## 1. 项目简介
MiniNews 是一个功能精简的新闻/公告浏览系统：
- 用户端（uniapp）：注册、登录、浏览新闻与公告（两栏）、查看详情
- 管理后台（Spring Boot 模板页）：管理员发布与管理新闻/公告
- 新闻支持：草稿(DRAFT) / 上线(PUBLISHED) / 下线(OFFLINE) / 删除(软删)

## 2. 技术栈
- 后端：Java + Spring Boot
- 数据库：MySQL 8.x
- 用户端：uniapp（同一套代码编译 H5 + App）

## 3. 本地启动（概览）
1) 执行数据库脚本：`db/init.sql`（创建库表、插入默认管理员）
2) 启动后端：`server/`（Spring Boot）
3) 启动 uniapp：`uniapp/`（HBuilderX 运行到 H5 或 App）

## 4. 默认账号
- 管理员：
  - username: admin
  - password: Admin@123456
  - role: ADMIN

> 注意：init.sql 中保存的是 password_hash（哈希值），不是明文密码。
> 后端登录时请使用 BCrypt 校验。
