package com.hocai.controller;

import com.hocai.entity.AssignmentSubmission;
import com.hocai.entity.User;
import com.hocai.service.AssignmentService;
import com.hocai.service.CompletionService;
import com.hocai.service.SubmissionService;
import com.hocai.service.SurveyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/student/assignments")
public class StudentAssignmentController {

    private final AssignmentService assignmentService;
    private final CompletionService completionService;
    private final SurveyService surveyService;
    private final SubmissionService submissionService;

    public StudentAssignmentController(AssignmentService assignmentService,
                                       CompletionService completionService,
                                       SurveyService surveyService,
                                       SubmissionService submissionService) {
        this.assignmentService = assignmentService;
        this.completionService = completionService;
        this.surveyService = surveyService;
        this.submissionService = submissionService;
    }

    @GetMapping
    public String list(HttpSession session, Model model) {
        User student = currentStudent(session);
        model.addAttribute("assignments", assignmentService.findAll());
        model.addAttribute("completedIds", completionService.getCompletedAssignmentIds(student.getId()));
        return "student/assignments";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        User student = currentStudent(session);
        model.addAttribute("assignment", assignmentService.get(id));
        model.addAttribute("completed", completionService.isCompleted(student.getId(), id));
        model.addAttribute("questions", surveyService.getQuestions(id));
        model.addAttribute("myAnswers", surveyService.getStudentAnswers(id, student.getId()));
        model.addAttribute("surveyDone", surveyService.isSurveyCompleted(id, student.getId()));
        model.addAttribute("submission", submissionService.find(student.getId(), id).orElse(null));
        return "student/assignment-detail";
    }

    @PostMapping("/{id}/complete")
    public String complete(@PathVariable Long id, HttpSession session, RedirectAttributes redirect) {
        User student = currentStudent(session);
        completionService.markCompleted(student.getId(), id);
        redirect.addFlashAttribute("success", "Đã đánh dấu hoàn thành bài tập");
        return "redirect:/student/assignments/" + id;
    }

    /** Form gửi các input tên answer_{idCauHoi}. */
    @PostMapping("/{id}/survey")
    public String submitSurvey(@PathVariable Long id,
                               @RequestParam Map<String, String> params,
                               HttpSession session,
                               RedirectAttributes redirect) {
        User student = currentStudent(session);
        Map<Long, String> answers = new HashMap<>();
        for (Map.Entry<String, String> e : params.entrySet()) {
            if (e.getKey().startsWith("answer_")) {
                try {
                    answers.put(Long.parseLong(e.getKey().substring("answer_".length())), e.getValue());
                } catch (NumberFormatException ignored) {
                    // bỏ qua key không hợp lệ
                }
            }
        }
        surveyService.saveAnswers(id, student.getId(), answers);
        if (surveyService.isSurveyCompleted(id, student.getId())) {
            redirect.addFlashAttribute("success", "Đã lưu khảo sát. Bạn có thể gửi bài làm bằng ảnh rồi!");
        } else {
            redirect.addFlashAttribute("error", "Vui lòng trả lời tất cả các câu hỏi để mở bước gửi bài làm");
        }
        return "redirect:/student/assignments/" + id;
    }

    /** Gửi hoặc gửi lại bài làm bằng ảnh (chỉ khi đã trả lời đủ khảo sát). */
    @PostMapping("/{id}/submit")
    public String submitWork(@PathVariable Long id,
                             @RequestParam(value = "image", required = false) MultipartFile image,
                             HttpSession session,
                             RedirectAttributes redirect) {
        User student = currentStudent(session);
        try {
            AssignmentSubmission s = submissionService.submit(student.getId(), id, image);
            redirect.addFlashAttribute("success",
                    s.getSubmitCount() > 1 ? "Đã gửi lại bài làm thành công!" : "Đã gửi bài làm thành công!");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        } catch (IOException e) {
            redirect.addFlashAttribute("error", "Không lưu được ảnh, vui lòng thử lại");
        }
        return "redirect:/student/assignments/" + id;
    }

    private User currentStudent(HttpSession session) {
        return (User) session.getAttribute(AuthController.SESSION_USER);
    }
}
