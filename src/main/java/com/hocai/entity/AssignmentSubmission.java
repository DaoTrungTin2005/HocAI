package com.hocai.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Bài làm bằng ảnh của học sinh. Mỗi học sinh có 1 bài nộp cho mỗi bài tập; gửi lại sẽ ghi đè ảnh cũ. */
@Entity
@Table(name = "assignment_submissions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "assignment_id"}))
public class AssignmentSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    /** Tên file ảnh trong thư mục uploads. */
    @Column(nullable = false)
    private String imagePath;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    /** Số lần gửi (gửi lại thì tăng lên), hữu ích cho phân tích hành vi sau này. */
    @Column(nullable = false)
    private int submitCount = 1;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    public Assignment getAssignment() { return assignment; }
    public void setAssignment(Assignment assignment) { this.assignment = assignment; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public int getSubmitCount() { return submitCount; }
    public void setSubmitCount(int submitCount) { this.submitCount = submitCount; }
}
