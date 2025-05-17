# Taskify API Documentation

Ứng dụng quản lý công việc cho từng user cá nhân, backend cung cấp các API để:

- Xác thực người dùng (đăng nhập, đăng xuất)
- Lấy dữ liệu task
- Cập nhật task
- Cập nhật thông tin người dùng

---

## 1. Mô hình dữ liệu

### User

| Trường     | Kiểu dữ liệu | Mô tả                              |
|------------|--------------|-----------------------------------|
| id         | String       | ID duy nhất của user               |
| username   | String       | Tên đăng nhập (username)           |
| email      | String       | Email                             |
| password   | String       | Mật khẩu (lưu hashed trên backend) |

---

### Task

| Trường      | Kiểu dữ liệu       | Mô tả                             |
|-------------|--------------------|----------------------------------|
| id          | String             | ID duy nhất của task             |
| userId      | String             | ID của user sở hữu task này       |
| title       | String             | Tiêu đề công việc                |
| description | String             | Mô tả công việc                  |
| subtasks    | List<'Subtask>     | Danh sách các subtask            |
| createAt    | String (ISO8601)   | Ngày tạo task                   |
| dueDate     | String (ISO8601)   | Ngày giờ thực hiện task             |
| type        | String             | Loại việc làm (họp, du lịch, ...) |
| isSuccess   | Boolean            | Đánh dấu hoàn thành              |

---

### Subtask

| Trường      | Kiểu dữ liệu | Mô tả                             |
|-------------|--------------|----------------------------------|
| title       | String       | Tiêu đề subtask                 |
| subtaskDes  | String       | Mô tả chi tiết của subtask      |

---

## 2. Authentication API

### POST /api/auth/login

**Mô tả:** Đăng nhập user, trả về token để xác thực các request tiếp theo.

- Request body:

```json
{
  "username": "user1",
  "password": "pass123"
}
```

---

- Response thành công (200 OK)
``` json
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

### POST /api/auth/logout
**Mô tả: Đăng xuất user, invalid token.**
- Headers:
``` makefile
Authorization: Bearer <token>
```
- Response thành công (200 OK):
``` json
{
  "message": "Logged out successfully"
}
```

---

### POST /api/auth/register
**Mô tả: Tạo tài khoản người dùng mới. Trả về thông tin user và token sau khi đăng ký thành công.**
- Headers:
``` makefile
Authorization: Bearer <token>
```
- Request body:
``` json
{
  "username": "newuser",
  "email": "newuser@example.com",
  "password": "securePassword123"
}
```
- Response thành công (201 Created):
``` json
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
``` json
{
  "error": "Email already exists"
}
```

---

### User API
**GET /api/user/me**
- Mô tả: Lấy thông tin user hiện tại.
- Headers:
``` makefile
Authorization: Bearer <token>
```
- Response thành công (200 OK)
``` json
{
  "id": "user123",
  "username": "user1",
  "email": "user1@example.com"
}
```

---

**PUT /api/user/me**
- Mô tả: Cập nhật thông tin user (username, email, password)
- Headers:
``` makefile
Authorization: Bearer <token>
Content-Type: application/json
```
- Request Body (JSON):
``` json
{
  "username": "newusername",
  "email": "newemail@example.com",
  "password": "newpassword123"
}
```
- Response thành công (200 OK)
``` json
{
  "id": "user123",
  "username": "newusername",
  "email": "newemail@example.com"
}
```

---

### Task API
** GET /api/tasks
- Mô tả: Lấy danh sách tất cả task của user hiện tại.
- Headers:
``` makefile
Authorization: Bearer <token>
```
- Response thành công (200 OK):
``` json
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
  },
  {
    "id": "task002",
    "userId": "user123",
    "title": "Task 2",
    "description": "Mô tả công việc",
    "subtasks": [
      {
        "title": "Subtask 2",
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

**POST /api/tasks
- Mô tả: Tạo task mới.
- Headers:
``` makefile
Authorization: Bearer <token>
Content-Type: application/json
```
- Request Body (JSON):
``` json
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
``` json
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
}
```

---

**PUT /api/tasks/{taskId}
- Mô tả: Cập nhật task.
- Headers:
``` makefile
Authorization: Bearer <token>
Content-Type: application/json
```
- Request Body (JSON):
``` json
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
``` json
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

### DELETE /api/tasks/{taskId}
- Mô tả: Xóa task.
- Headers:
``` makefile
Authorization: Bearer <token>
```
- Response thành công (204 No Content): Không trả về nội dung.
