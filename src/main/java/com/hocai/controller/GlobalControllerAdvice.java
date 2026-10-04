package com.hocai.controller;

import com.hocai.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Đưa user đang đăng nhập vào mọi view dưới tên "currentUser" và xử lý lỗi ảnh quá lớn. */
@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("currentUser")
    public User currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (User) session.getAttribute(AuthController.SESSION_USER);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleTooLarge(HttpServletRequest request, RedirectAttributes redirect) {
        redirect.addFlashAttribute("error", "Ảnh quá lớn, vui lòng chọn ảnh dưới 5MB");
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }
}
