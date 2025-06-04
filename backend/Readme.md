
# 📚 Task Management API Documentation

---

## Authentication API

### POST /api/v1/users/register

**Mô tả:** Đăng ký người dùng mới. Trả về thông tin user sau khi đăng ký thành công.

- Request Body:

```json
{
  "username": "test1",
  "email": "test1@example.com",
  "password": "securePassword123"
}
```

- Response thành công (201 Created):

```json
{
    "_id": "682d9b8af609b7322dbd7211",
    "email": "test1@example.com",
    "username": "test1",
    "createdAt": "2025-05-21T09:23:22.385Z",
    "updatedAt": "2025-05-21T09:23:22.385Z"
}
```

- Response lỗi (ví dụ: Email đã tồn tại):

```json
{
  "error": "Email already exists"
}
```

---

### POST /api/v1/users/login

**Mô tả:** Đăng nhập user, trả về token để xác thực các request tiếp theo.

- Request Body:

```json
{
  "email": "test1@example.com",
  "password": "securePassword123"
}
```

- Response thành công (200 OK):

```json
{
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2ODJkOWI4YWY2MDliNzMyMmRiZDcyMTEiLCJlbWFpbCI6InRlc3QxQGV4YW1wbGUuY29tIiwiaWF0IjoxNzQ3ODE5NzQ5LCJleHAiOjE3NDc4MjMzNDl9.haYFQ6-kGy7LPq04jo8QbZsF3QKdAn4Nj3jM7TcuMkM",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2ODJkOWI4YWY2MDliNzMyMmRiZDcyMTEiLCJlbWFpbCI6InRlc3QxQGV4YW1wbGUuY29tIiwiaWF0IjoxNzQ3ODE5NzQ5LCJleHAiOjE3NDkwMjkzNDl9._8m6jHBgpEAAFlzuYGj1Swbm87CfszoGJvaBQEDB9b4",
    "_id": "682d9b8af609b7322dbd7211",
    "email": "test1@example.com",
    "username": "test1",
    "createdAt": "2025-05-21T09:23:22.385Z",
    "updatedAt": "2025-05-21T09:23:22.385Z"
}
```
### POST /api/v1/users/logout

**Mô tả:** Đăng xuất

- Headers:

```makefile
Authorization: Bearer <token>
```

- Response thành công (200 OK):

```json
{
    "message": "Logout successfully!"
}
---
```
### GET /api/v1/users/

**Mô tả:** Lấy thông tin người dùng

- Headers:

```makefile
Authorization: Bearer <token>
```

- Response thành công (200 OK):

```json
{
    "_id": "682f2dcc3f4735b8a8910715",
    "email": "test10@example.com",
    "username": "nguyen",
    "createdAt": "2025-05-22T13:59:40.984Z",
    "updatedAt": "2025-05-31T06:30:39.278Z"
}
---
```
### POST /api/v1/users/

**Mô tả:**  Cập nhật thông tin user (username, email)

- Headers:

```makefile
Authorization: Bearer <token>
```
- Request Body:

```json
{
  "username": "newusername",
  "email": "newemail@example.com"
}
```

- Response thành công (200 OK):

```json
{
    "_id": "682f2dcc3f4735b8a8910715",
    "email": "newemail@example.com",
    "username": "newusername",
    "createdAt": "2025-05-22T13:59:40.984Z",
    "updatedAt": "2025-06-04T00:06:45.948Z"
}
```

### GET /api/v1/users/refresh_token

**Mô tả:** Làm mới token nếu token cũ sắp hết hạn hoặc đã hết hạn.

- Request Body:

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2ODJkOWI4YWY2MDliNzMyMmRiZDcyMTEiLCJlbWFpbCI6InRlc3QxQGV4YW1wbGUuY29tIiwiaWF0IjoxNzQ3ODE5OTc4LCJleHAiOjE3NDkwMjk1Nzh9.mB6muXHlKWVp6Z0UkIH9oNjJ2E0mE9A2wvNU_mxL7Tw"
}
```

- Response thành công (200 OK):

```json
{
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2ODJkOWI4YWY2MDliNzMyMmRiZDcyMTEiLCJlbWFpbCI6InRlc3QxQGV4YW1wbGUuY29tIiwiaWF0IjoxNzQ3ODIwMTQ1LCJleHAiOjE3NDc4MjM3NDV9.4O2xiZUf1JX6Zxw5h3Wo6tGreDzApLSa8XUIcDaKYZA"
}
```

---

---

## Task API

### GET /api/v1/tasks

**Mô tả:** Lấy danh sách tất cả task của user hiện tại.

- Headers:

```makefile
Authorization: Bearer <token>
```

- Response thành công (200 OK):

```json
[
    {
        "_id": "683f0b69bef1c34028cdf037",
        "userId": "682f2dcc3f4735b8a8910715",
        "title": "Task mới ",
        "description": "Mô tả task mới",
        "taskDate": "2025-06-07",
        "taskTime": "19:30:00",
        "type": "Meeting",
        "isSuccess": false,
        "subtasks": [],
        "createAt": "2025-06-03T14:49:13.558Z",
        "__v": 0
    }
]
```

---

### POST /api/v1/tasks

**Mô tả:** Tạo task mới.

- Headers:

```makefile
Authorization: Bearer <token>
Content-Type: application/json
```

- Request Body:

```json
{
  "title": "Task mới sửa",
  "description": "Mô tả task mới",
  "taskDate": "2025-05-26",
  "taskTime": "19:30:00",
  "type": "Meeting",
  "isSuccess": false
}
```

- Response thành công (201 Created):

```json
{
    "userId": "682f2dcc3f4735b8a8910715",
    "title": "Task mới sửa",
    "description": "Mô tả task mới",
    "taskDate": "2025-05-16",
    "taskTime": "19:30:00",
    "type": "Meeting",
    "isSuccess": false,
    "_id": "6832a1f1727015191954a61b",
    "subtasks": [],
    "createAt": "2025-05-25T04:52:01.355Z",
    "__v": 0
}
```

---

### PUT /api/v1/tasks/:taskId

**Mô tả:** Cập nhật task.

- Headers:

```makefile
Authorization: Bearer <token>
Content-Type: application/json
```

- Request Body:

```json
{
    "title": "Task mới sửa",
    "description": "Mô tả task mới",
    "taskDate": "2025-05-16",
    "taskTime": "19:30:00",
    "type": "Meeting",
    "isSuccess": true,
    "subtasks": [{
      "title": "Subtask mới",
      "subtaskDes": "Mô tả subtask sửa"
    },
    {
      "title": "Subtask mới",
      "subtaskDes": "Mô tả subtask mới"
    }],
    "createAt": "2025-05-25T04:52:01.355Z",
    "__v": 0
}
```

- Response thành công (200 OK):

```json
{
    "_id": "6837d1e974f254aea8ee7af5",
    "userId": "682f2dcc3f4735b8a8910715",
    "title": "Task mới sửa",
    "description": "Mô tả task mới",
    "taskDate": "2025-05-16",
    "taskTime": "19:30:00",
    "type": "Meeting",
    "isSuccess": true,
    "subtasks": [
        {
            "title": "Subtask mới",
            "subtaskDes": "Mô tả subtask sửa"
        },
        {
            "title": "Subtask mới",
            "subtaskDes": "Mô tả subtask mới"
        }
    ],
    "createAt": "2025-05-25T04:52:01.355Z",
    "__v": 0
}
```

---

### DELETE /api/v1/tasks/:taskId

**Mô tả:** Xóa task.

- Headers:

```makefile
Authorization: Bearer <token>
```

- Response thành công (204 No Content): Không trả về nội dung.


---
