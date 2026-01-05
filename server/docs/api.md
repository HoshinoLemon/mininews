# MiniNews API（Auth & Security）

## 认证方式（JWT）
- Header：`Authorization: Bearer <token>`
- token 由 `/api/auth/login` 返回
- 受保护接口：
  - `/api/admin/**`：必须 ADMIN
  - `POST /api/news/{newsId}/comments`：必须已登录（USER/ADMIN 均可）

---

## 1) 注册
### POST /api/auth/register
Request Body:
```json
{
  "username": "user001",
  "password": "Passw0rd!"
}
