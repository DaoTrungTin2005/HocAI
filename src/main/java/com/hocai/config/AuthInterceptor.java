package com.hocai.config;

import com.hocai.controller.AuthController;
import com.hocai.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/** Chặn truy cập khi chưa đăng nhập hoặc sai vai trò. */
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute(AuthController.SESSION_USER);
        String ctx = request.getContextPath();
        String path = request.getRequestURI().substring(ctx.length());

        if (user == null) {
            response.sendRedirect(ctx + "/login");
            return false;
        }
        if (path.startsWith("/teacher") && !user.isTeacher()) {
            response.sendRedirect(ctx + "/");
            return false;
        }
        if (path.startsWith("/student") && user.isTeacher()) {
            response.sendRedirect(ctx + "/");
            return false;
        }
        return true;
    }
}
