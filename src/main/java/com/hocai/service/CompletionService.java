package com.hocai.service;

import com.hocai.entity.Assignment;
import com.hocai.entity.AssignmentCompletion;
import com.hocai.repository.AssignmentCompletionRepository;
import com.hocai.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CompletionService {

    private final AssignmentCompletionRepository completionRepository;
    private final UserRepository userRepository;
    private final AssignmentService assignmentService;

    public CompletionService(AssignmentCompletionRepository completionRepository,
                             UserRepository userRepository,
                             AssignmentService assignmentService) {
        this.completionRepository = completionRepository;
        this.userRepository = userRepository;
        this.assignmentService = assignmentService;
    }

    public boolean isCompleted(Long studentId, Long assignmentId) {
        return completionRepository.findByStudentIdAndAssignmentId(studentId, assignmentId).isPresent();
    }

    public Set<Long> getCompletedAssignmentIds(Long studentId) {
        return completionRepository.findByStudentId(studentId).stream()
                .map(c -> c.getAssignment().getId())
                .collect(Collectors.toSet());
    }

    public List<AssignmentCompletion> getCompletions(Long assignmentId) {
        return completionRepository.findByAssignmentIdOrderByCompletedAtAsc(assignmentId);
    }

    @Transactional
    public void markCompleted(Long studentId, Long assignmentId) {
        if (isCompleted(studentId, assignmentId)) {
            return;
        }
        Assignment assignment = assignmentService.get(assignmentId);
        AssignmentCompletion c = new AssignmentCompletion();
        c.setStudent(userRepository.getReferenceById(studentId));
        c.setAssignment(assignment);
        c.setCompletedAt(LocalDateTime.now());
        completionRepository.save(c);
    }
}
