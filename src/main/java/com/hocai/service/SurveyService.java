package com.hocai.service;

import com.hocai.entity.Assignment;
import com.hocai.entity.SurveyAnswer;
import com.hocai.entity.SurveyQuestion;
import com.hocai.entity.User;
import com.hocai.repository.SurveyAnswerRepository;
import com.hocai.repository.SurveyQuestionRepository;
import com.hocai.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SurveyService {

    private final SurveyQuestionRepository questionRepository;
    private final SurveyAnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final AssignmentService assignmentService;

    public SurveyService(SurveyQuestionRepository questionRepository,
                         SurveyAnswerRepository answerRepository,
                         UserRepository userRepository,
                         AssignmentService assignmentService) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
        this.assignmentService = assignmentService;
    }

    public List<SurveyQuestion> getQuestions(Long assignmentId) {
        return questionRepository.findByAssignmentIdOrderByIdAsc(assignmentId);
    }

    @Transactional
    public void addQuestion(Long assignmentId, String content) {
        Assignment assignment = assignmentService.get(assignmentId);
        SurveyQuestion q = new SurveyQuestion();
        q.setAssignment(assignment);
        q.setContent(content.trim());
        q.setCreatedAt(LocalDateTime.now());
        questionRepository.save(q);
    }

    /** Giáo viên: tất cả câu trả lời của bài tập, gom theo id câu hỏi. */
    public Map<Long, List<SurveyAnswer>> getAnswersByQuestion(Long assignmentId) {
        return answerRepository.findByQuestionAssignmentIdOrderByAnsweredAtDesc(assignmentId).stream()
                .collect(Collectors.groupingBy(a -> a.getQuestion().getId(),
                        LinkedHashMap::new, Collectors.toList()));
    }

    /** Học sinh: câu trả lời đã nộp của chính mình, key là id câu hỏi. */
    public Map<Long, SurveyAnswer> getStudentAnswers(Long assignmentId, Long studentId) {
        return answerRepository.findByQuestionAssignmentIdAndStudentId(assignmentId, studentId).stream()
                .collect(Collectors.toMap(a -> a.getQuestion().getId(), Function.identity()));
    }

    /** True nếu bài không có khảo sát, hoặc học sinh đã trả lời đủ mọi câu hỏi. */
    public boolean isSurveyCompleted(Long assignmentId, Long studentId) {
        List<SurveyQuestion> questions = getQuestions(assignmentId);
        if (questions.isEmpty()) {
            return true;
        }
        Map<Long, SurveyAnswer> answers = getStudentAnswers(assignmentId, studentId);
        return questions.stream().allMatch(q -> answers.containsKey(q.getId()));
    }

    @Transactional
    public void saveAnswers(Long assignmentId, Long studentId, Map<Long, String> answers) {
        Map<Long, SurveyQuestion> valid = getQuestions(assignmentId).stream()
                .collect(Collectors.toMap(SurveyQuestion::getId, Function.identity()));
        User student = userRepository.getReferenceById(studentId);

        for (Map.Entry<Long, String> entry : answers.entrySet()) {
            SurveyQuestion question = valid.get(entry.getKey());
            String text = entry.getValue();
            if (question == null || text == null || text.isBlank()) {
                continue;
            }
            SurveyAnswer answer = answerRepository
                    .findByQuestionIdAndStudentId(question.getId(), studentId)
                    .orElseGet(SurveyAnswer::new);
            answer.setQuestion(question);
            answer.setStudent(student);
            answer.setContent(text.trim());
            answer.setAnsweredAt(LocalDateTime.now());
            answerRepository.save(answer);
        }
    }
}
