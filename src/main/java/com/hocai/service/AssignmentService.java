package com.hocai.service;

import com.hocai.entity.Assignment;
import com.hocai.entity.User;
import com.hocai.repository.AssignmentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final FileStorageService fileStorageService;

    public AssignmentService(AssignmentRepository assignmentRepository,
                             FileStorageService fileStorageService) {
        this.assignmentRepository = assignmentRepository;
        this.fileStorageService = fileStorageService;
    }

    public List<Assignment> findAll() {
        return assignmentRepository.findAllByOrderByDeadlineAsc();
    }

    public Assignment get(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài tập"));
    }

    @Transactional
    public Assignment create(String title, String description, LocalDateTime deadline,
                             MultipartFile image, User creator) throws IOException {
        Assignment a = new Assignment();
        a.setTitle(title.trim());
        a.setDescription(description == null ? null : description.trim());
        a.setDeadline(deadline);
        a.setCreatedAt(LocalDateTime.now());
        a.setCreatedBy(creator);
        a.setImagePath(fileStorageService.storeImage(image));
        return assignmentRepository.save(a);
    }
}
