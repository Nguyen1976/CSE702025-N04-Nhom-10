
# 📚 Task Management API Documentation

---

## Authentication API

### POST /api/v1/users/register


- Response thành công (201 Created):

```json
{

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



- Response thành công (200 OK):



---

### GET /api/v1/users/refresh_token

**Mô tả:** Làm mới token nếu token cũ sắp hết hạn hoặc đã hết hạn.


- Response thành công (200 OK):


```json
[

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
