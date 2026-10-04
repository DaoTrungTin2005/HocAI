# HỌC AI (v1 - chưa tích hợp AI)

Spring Boot 3 + Spring MVC + Thymeleaf + Spring Data JPA + MySQL + Bootstrap 5.

## Chạy project
1. Cài JDK 17, Maven, MySQL (đang chạy ở localhost:3306).
2. Sửa `spring.datasource.username/password` trong `src/main/resources/application.properties`.
3. `mvn spring-boot:run`
4. Mở http://localhost:8080

Database `hocai` tự được tạo, bảng tự sinh (ddl-auto=update).

## Tài khoản mẫu (mật khẩu 123456)
- teacher (giáo viên)
- hs1 (học sinh, nhóm EXPERIMENT)
- hs2 (học sinh, nhóm CONTROL)

Ảnh upload được lưu ở thư mục `uploads/` cạnh nơi chạy ứng dụng.

## Luồng học sinh
1. Trả lời **tất cả** câu hỏi khảo sát (Bước 1).
2. Bước 2 (gửi bài làm bằng ảnh) mở khóa; có thể gửi lại nhiều lần, ảnh mới ghi đè ảnh cũ.
3. Gửi bài thành công thì bài tập tự động được đánh dấu hoàn thành.
Giáo viên xem ảnh bài làm ở trang chi tiết bài tập.
