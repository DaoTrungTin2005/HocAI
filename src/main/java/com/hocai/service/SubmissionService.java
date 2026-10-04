package com.hocai.service;

import com.hocai.entity.Assignment;
import com.hocai.entity.AssignmentSubmission;
import com.hocai.repository.AssignmentSubmissionRepository;
import com.hocai.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SubmissionService {

    private final AssignmentSubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final AssignmentService assignmentService;
    private final SurveyService surveyService;
    private final CompletionService completionService;
    private final FileStorageService fileStorageService;

    public SubmissionService(AssignmentSubmissionRepository submissionRepository,
                             UserRepository userRepository,
                             AssignmentService assignmentService,
                             SurveyService surveyService,
                             CompletionService completionService,
                             FileStorageService fileStorageService) {
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
        this.assignmentService = assignmentService;
        this.surveyService = surveyService;
        this.completionService = completionService;
        this.fileStorageService = fileStorageService;
    }

    public Optional<AssignmentSubmission> find(Long studentId, Long assignmentId) {
        return submissionRepository.findByStudentIdAndAssignmentId(studentId, assignmentId);
    }

    public List<AssignmentSubmission> getSubmissions(Long assignmentId) {
        return submissionRepository.findByAssignmentIdOrderBySubmittedAtDesc(assignmentId);
    }

    /**
     * Học sinh gửi (hoặc gửi lại) bài làm bằng ảnh.
     * Chỉ được gửi sau khi đã trả lời đủ khảo sát. Gửi thành công thì bài tập được đánh dấu hoàn thành.
     */
    @Transactional
    public AssignmentSubmission submit(Long studentId, Long assignmentId, MultipartFile image) throws IOException {
        Assignment assignment = assignmentService.get(assignmentId);

        if (!surveyService.isSurveyCompleted(assignmentId, studentId)) {
            throw new IllegalStateException("Bạn cần trả lời hết khảo sát trước khi gửi bài làm");
        }
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn ảnh bài làm");
        }

        String newFile = fileStorageService.storeImage(image);

        AssignmentSubmission submission = submissionRepository
                .findByStudentIdAndAssignmentId(studentId, assignmentId)
                .orElse(null);
        String oldFile = null;
        if (submission == null) {
            submission = new AssignmentSubmission();
            submission.setStudent(userRepository.getReferenceById(studentId));
            submission.setAssignment(assignment);
            submission.setSubmitCount(1);
        } else {
            oldFile = submission.getImagePath();
            submission.setSubmitCount(submission.getSubmitCount() + 1);
        }
        submission.setImagePath(newFile);
        submission.setSubmittedAt(LocalDateTime.now());
        submission = submissionRepository.save(submission);

        if (oldFile != null) {
            fileStorageService.delete(oldFile);
        }
        completionService.markCompleted(studentId, assignmentId);
        return submission;
    }
}
