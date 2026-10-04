package com.hocai.controller;

import com.hocai.entity.User;
import com.hocai.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {

    public static final String SESSION_USER = "currentUser";

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(HttpSession session) {
        User user = (User) session.getAttribute(SESSION_USER);
        if (user == null) {
            return "redirect:/login";
        }
        return redirectByRole(user);
    }

    @GetMapping("/login")
    public String loginForm(HttpSession session) {
        User user = (User) session.getAttribute(SESSION_USER);
        if (user != null) {
            return redirectByRole(user);
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpServletRequest request,
                        Model model) {
        Optional<User> user = userService.authenticate(username, password);
        if (user.isEmpty()) {
            model.addAttribute("error", "Sai tên đăng nhập hoặc mật khẩu");
            model.addAttribute("username", username);
            return "login";
        }
        HttpSession session = request.getSession();
        request.changeSessionId();
        session.setAttribute(SESSION_USER, user.get());
        return redirectByRole(user.get());
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    private String redirectByRole(User user) {
        return user.isTeacher() ? "redirect:/teacher/assignments" : "redirect:/student/assignments";
    }
}
