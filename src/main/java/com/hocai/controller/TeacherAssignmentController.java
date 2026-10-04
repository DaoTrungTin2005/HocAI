package com.hocai.controller;

import com.hocai.entity.Assignment;
import com.hocai.entity.User;
import com.hocai.service.AssignmentService;
import com.hocai.service.CompletionService;
import com.hocai.service.SubmissionService;
import com.hocai.service.SurveyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/teacher/assignments")
public class TeacherAssignmentController {

    private final AssignmentService assignmentService;
    private final SurveyService surveyService;
    private final CompletionService completionService;
    private final SubmissionService submissionService;

    public TeacherAssignmentController(AssignmentService assignmentService,
                                       SurveyService surveyService,
                                       CompletionService completionService,
                                       SubmissionService submissionService) {
        this.assignmentService = assignmentService;
        this.surveyService = surveyService;
        this.completionService = completionService;
        this.submissionService = submissionService;
    }

    @GetMapping
    public String list(Model model) {
        List<Assignment> all = assignmentService.findAll();
        LocalDateTime now = LocalDateTime.now();
        long upcoming = all.stream().filter(a -> a.getDeadline().isAfter(now)).count();
        model.addAttribute("assignments", all);
        model.addAttribute("totalCount", all.size());
        model.addAttribute("upcomingCount", upcoming);
        model.addAttribute("overdueCount", all.size() - upcoming);
        return "teacher/assignments";
    }

    @GetMapping("/new")
    public String newForm() {
        return "teacher/assignment-form";
    }

    @PostMapping
    public String create(@RequestParam(required = false) String title,
                         @RequestParam(required = false) String description,
                         @RequestParam(required = false)
                         @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime deadline,
                         @RequestParam(value = "image", required = false) MultipartFile image,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirect) {
        // Giữ lại dữ liệu đã nhập nếu có lỗi
        model.addAttribute("title", title);
        model.addAttribute("description", description);
        model.addAttribute("deadline", deadline);

        if (title == null || title.isBlank()) {
            model.addAttribute("error", "Vui lòng nhập tên bài tập");
            return "teacher/assignment-form";
        }
        if (deadline == null) {
            model.addAttribute("error", "Vui lòng chọn deadline");
            return "teacher/assignment-form";
        }
        try {
            User teacher = (User) session.getAttribute(AuthController.SESSION_USER);
            Assignment a = assignmentService.create(title, description, deadline, image, teacher);
            redirect.addFlashAttribute("success", "Đã tạo bài tập thành công");
            return "redirect:/teacher/assignments/" + a.getId();
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "teacher/assignment-form";
        } catch (IOException e) {
            model.addAttribute("error", "Không lưu được ảnh, vui lòng thử lại");
            return "teacher/assignment-form";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("assignment", assignmentService.get(id));
        model.addAttribute("questions", surveyService.getQuestions(id));
        model.addAttribute("answersByQuestion", surveyService.getAnswersByQuestion(id));
        model.addAttribute("completions", completionService.getCompletions(id));
        model.addAttribute("submissions", submissionService.getSubmissions(id));
        return "teacher/assignment-detail";
    }

    @PostMapping("/{id}/questions")
    public String addQuestion(@PathVariable Long id,
                              @RequestParam(required = false) String content,
                              RedirectAttributes redirect) {
        if (content == null || content.isBlank()) {
            redirect.addFlashAttribute("error", "Nội dung câu hỏi không được để trống");
        } else {
            surveyService.addQuestion(id, content);
            redirect.addFlashAttribute("success", "Đã thêm câu hỏi khảo sát");
        }
        return "redirect:/teacher/assignments/" + id;
    }
}
