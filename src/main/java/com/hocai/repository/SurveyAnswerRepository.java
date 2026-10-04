package com.hocai.repository;

import com.hocai.entity.SurveyAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyAnswerRepository extends JpaRepository<SurveyAnswer, Long> {
    List<SurveyAnswer> findByQuestionAssignmentIdOrderByAnsweredAtDesc(Long assignmentId);
    List<SurveyAnswer> findByQuestionAssignmentIdAndStudentId(Long assignmentId, Long studentId);
    Optional<SurveyAnswer> findByQuestionIdAndStudentId(Long questionId, Long studentId);
}
