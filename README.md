# 📄 Mô tả dự án: Mobile App Quản Lý Công Việc - Taskify

## 1. Giới thiệu
Dự án **Mobile App Quản Lý Công Việc** là một ứng dụng Android hỗ trợ người dùng quản lý công việc hằng ngày hiệu quả với các tính năng như: thêm công việc, nhắc nhở, phân loại ưu tiên, theo dõi tiến độ, đăng ký/đăng nhập.

---

## 2. Thành viên & Phân công

| Họ tên                  | Vai trò |
|-------------------------|--------|
| Ngô Đặng Nhật Dũng      | Thiết kế giao diện (Figma, Jetpack Compose), viết tài liệu |
| Bùi Anh Quốc            | Lập trình UI Android |
| Hoàng Lê Đức Huy        | Lập trình backend (API) |
| Nguyễn Hà Nguyên        | Lập trình backend (Auth, JWT, cấu hình hệ thống) |

---

## 3. Tài liệu dự án

### 🔹 1. [Kế hoạch dự án (plan.docx)](./plan.docx)
- Bao gồm lịch trình phát triển theo 8 tuần.
- Phân công rõ ràng vai trò từng thành viên.
- Ghi chú kết quả kỳ vọng từng tuần.
- Mô tả công nghệ sử dụng và đầu ra cuối cùng.

### 🔹 2. [Đặc tả phần mềm (TÀI LIỆU ĐẶC TẢ.pptx)](./TÀI%20LIỆU%20ĐẶC%20TẢ.pptx)
- Mục tiêu & phạm vi dự án
- Yêu cầu chức năng & phi chức năng
- Use case chính
- Mô hình dữ liệu và tương tác với người dùng

### 🔹 3. [Postman Collection (Note_App_KTPM.postman_collection.json)](./Note_App_KTPM.postman_collection.json)
- Bộ API RESTful backend Node.js
- Hỗ trợ các chức năng:
  - Đăng ký / Đăng nhập / Làm mới token / Đăng xuất
  - Tạo / Đọc / Cập nhật / Xóa công việc
  - Quản lý thông tin người dùng

---

## 4. Kiến trúc hệ thống

- **Frontend:** Android (Kotlin + Jetpack Compose)
- **Backend:** Node.js + Express
- **Database:** 
  - MongoDB (cloud)
  - Room (local on-device)
- **Authentication:** JWT token

---

## 5. Output cuối cùng

- ✅ Tài liệu phân tích và đặc tả yêu cầu
- ✅ Sơ đồ UML: Use case, Sequence, State
- ✅ UI thiết kế (Figma) và Compose code
- ✅ Source code (Android + Backend)
- ✅ Video demo, báo cáo cuối kỳ, khảo sát người dùng
- ✅ Toàn bộ tài liệu được đẩy lên GitHub

---

## 6. Liên kết liên quan

- 🔗 [Prototype Figma (Giao diện)](https://www.figma.com/design/Z3vkqzw8DTZlR8S3SXsdfm/Untitled?node-id=0-1&p=f&t=Mv9vNH2SOVS3ne2y-0)
