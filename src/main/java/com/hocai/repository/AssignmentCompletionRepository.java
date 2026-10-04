package com.hocai.repository;

import com.hocai.entity.AssignmentCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentCompletionRepository extends JpaRepository<AssignmentCompletion, Long> {
    Optional<AssignmentCompletion> findByStudentIdAndAssignmentId(Long studentId, Long assignmentId);
    List<AssignmentCompletion> findByStudentId(Long studentId);
    List<AssignmentCompletion> findByAssignmentIdOrderByCompletedAtAsc(Long assignmentId);
}
