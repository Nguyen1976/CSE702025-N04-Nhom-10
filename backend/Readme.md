
# 📚 Task Management API Documentation

---

## Authentication API

### POST /api/v1/users/register

**Mô tả:** Đăng ký người dùng mới. Trả về token và thông tin user sau khi đăng ký thành công.

- Request Body:

```json
{
  "username": "newuser",
  "email": "newuser@example.com",
  "password": "securePassword123"
}
```

- Response thành công (201 Created):

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "user456",
    "username": "newuser",
    "email": "newuser@example.com"
  }
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
  "username": "user1",
  "password": "pass123"
}
```

- Response thành công (200 OK):

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "user123",
    "username": "user1",
    "email": "user1@example.com"
  }
}
```

---

### GET /api/v1/users/refresh_token

**Mô tả:** Làm mới token nếu token cũ sắp hết hạn hoặc đã hết hạn.

- Response thành công (200 OK):

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

## User API

### GET /api/v1/users/me

**Mô tả:** Lấy thông tin user hiện tại.

- Headers:

```makefile
Authorization: Bearer <token>
```

- Response thành công (200 OK):

```json
{
  "id": "user123",
  "username": "user1",
  "email": "user1@example.com"
}
```

---

### PUT /api/v1/users/me

**Mô tả:** Cập nhật thông tin user (username, email, password)

- Headers:

```makefile
Authorization: Bearer <token>
Content-Type: application/json
```

- Request Body:

```json
{
  "username": "newusername",
  "email": "newemail@example.com",
  "password": "newpassword123"
}
```

- Response thành công (200 OK):

```json
{
  "id": "user123",
  "username": "newusername",
  "email": "newemail@example.com"
}
```

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
    "id": "task001",
    "userId": "user123",
    "title": "Task 1",
    "description": "Mô tả công việc",
    "subtasks": [
      {
        "title": "Subtask 1",
        "subtaskDes": "Mô tả subtask 1"
      }
    ],
    "createAt": "2025-05-16T08:30:00Z",
    "dueDate": "2025-05-16T08:35:00Z",
    "type": "Meeting",
    "isSuccess": false
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
  "title": "Task mới",
  "description": "Mô tả task mới",
  "subtasks": [
    {
      "title": "Subtask 1",
      "subtaskDes": "Mô tả chi tiết subtask"
    }
  ],
  "createAt": "2025-05-16T08:30:00Z",
  "dueDate": "2025-05-16T08:35:00Z",
  "type": "Meeting",
  "isSuccess": false
}
```

- Response thành công (201 Created):

```json
{
  "id": "task003",
  "userId": "user123",
  "title": "Task mới",
  "description": "Mô tả task mới",
  "subtasks": [
    {
      "title": "Subtask 1",
      "subtaskDes": "Mô tả chi tiết subtask"
    }
  ],
  "createAt": "2025-05-16T08:30:00Z",
  "dueDate": "2025-05-16T08:35:00Z",
  "type": "Meeting",
  "isSuccess": false
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
  "title": "Task đã sửa",
  "description": "Mô tả cập nhật",
  "subtasks": [
    {
      "title": "Subtask sửa",
      "subtaskDes": "Mô tả subtask sửa"
    },
    {
      "title": "Subtask mới",
      "subtaskDes": "Mô tả subtask mới"
    }
  ],
  "createAt": "2025-05-16T08:35:00Z",
  "dueDate": "2025-05-16T08:50:00Z",
  "type": "Travel",
  "isSuccess": true
}
```

- Response thành công (200 OK):

```json
{
  "id": "task003",
  "userId": "user123",
  "title": "Task đã sửa",
  "description": "Mô tả cập nhật",
  "subtasks": [
    {
      "title": "Subtask sửa",
      "subtaskDes": "Mô tả subtask sửa"
    },
    {
      "title": "Subtask mới",
      "subtaskDes": "Mô tả subtask mới"
    }
  ],
  "createAt": "2025-05-16T08:35:00Z",
  "dueDate": "2025-05-16T08:50:00Z",
  "type": "Travel",
  "isSuccess": true
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

## Tổng kết cấu trúc định tuyến

```js
// app.js
app.use('/api/v1', APIs_v1)

// APIs_v1
Router.use('/users', userRoute)
Router.use('/tasks', taskRoute)
```

---

## Ghi chú

- Tất cả các route yêu cầu xác thực (`Authorization: Bearer <token>`) đều dùng middleware `authMiddleware.isAuthorized`.
- Request body đều cần header `Content-Type: application/json`.

---
