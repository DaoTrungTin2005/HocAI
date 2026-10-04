package com.hocai.repository;

import com.hocai.entity.AssignmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {
    Optional<AssignmentSubmission> findByStudentIdAndAssignmentId(Long studentId, Long assignmentId);
    List<AssignmentSubmission> findByAssignmentIdOrderBySubmittedAtDesc(Long assignmentId);
}
