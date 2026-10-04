package com.hocai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/** Lưu và xóa file ảnh trong thư mục uploads (dùng chung cho ảnh bài tập và ảnh bài làm). */
@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "gif", "webp");

    private final Path root;

    public FileStorageService(@Value("${app.upload-dir}") String uploadDir) {
        this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new UncheckedIOException("Không tạo được thư mục upload: " + root, e);
        }
    }

    public Path getRoot() {
        return root;
    }

    /** Lưu ảnh, trả về tên file mới; trả về null nếu không có file. */
    public String storeImage(MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            return null;
        }
        String original = image.getOriginalFilename() == null ? "" : image.getOriginalFilename();
        int dot = original.lastIndexOf('.');
        String ext = dot >= 0 ? original.substring(dot + 1).toLowerCase(Locale.ROOT) : "";
        String contentType = image.getContentType();
        if (!ALLOWED_EXT.contains(ext) || contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Chỉ chấp nhận ảnh JPG, PNG, GIF hoặc WEBP");
        }
        Files.createDirectories(root);
        String fileName = UUID.randomUUID() + "." + ext;
        image.transferTo(root.resolve(fileName));
        return fileName;
    }

    public void delete(String fileName) {
        if (fileName == null || fileName.contains("/") || fileName.contains("\\") || fileName.contains("..")) {
            return;
        }
        try {
            Files.deleteIfExists(root.resolve(fileName));
        } catch (IOException ignored) {
            // không xóa được file cũ thì bỏ qua
        }
    }
}
