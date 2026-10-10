package com.learnhub.dto;

import com.learnhub.entity.Submission;
import com.learnhub.entity.SubmissionStatus;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class SubmissionDto {

    private Long id;
    private Long assignmentId;
    private String assignmentTitle;
    private Long courseId;
    private String courseTitle;
    private BigDecimal maxMarks;
    private UserDto student;
    private String textResponse;
    private String filePath;
    private String fileName;
    private LocalDateTime submittedAt;
    private SubmissionStatus status;
    private BigDecimal marksAwarded;
    private String feedback;
    private LocalDateTime gradedAt;
    private UserDto gradedBy;
    private LocalDateTime updatedAt;
    private BigDecimal percentage;
    private boolean isGraded;

    public SubmissionDto() {
    }

    public static SubmissionDto fromEntity(Submission submission) {
        if (submission == null) return null;
        SubmissionDto dto = new SubmissionDto();
        dto.setId(submission.getId());
        if (submission.getAssignment() != null) {
            dto.setAssignmentId(submission.getAssignment().getId());
            dto.setAssignmentTitle(submission.getAssignment().getTitle());
            dto.setMaxMarks(submission.getAssignment().getMaxMarks());
            if (submission.getAssignment().getCourse() != null) {
                dto.setCourseId(submission.getAssignment().getCourse().getId());
                dto.setCourseTitle(submission.getAssignment().getCourse().getTitle());
            }
        }
        dto.setStudent(UserDto.fromEntity(submission.getStudent()));
        dto.setTextResponse(submission.getTextResponse());
        dto.setFilePath(submission.getFilePath());
        dto.setFileName(submission.getFileName());
        dto.setSubmittedAt(submission.getSubmittedAt());
        dto.setStatus(submission.getStatus());
        dto.setMarksAwarded(submission.getMarksAwarded());
        dto.setFeedback(submission.getFeedback());
        dto.setGradedAt(submission.getGradedAt());
        dto.setGradedBy(UserDto.fromEntity(submission.getGradedBy()));
        dto.setUpdatedAt(submission.getUpdatedAt());

        boolean graded = submission.getStatus() == SubmissionStatus.GRADED && submission.getMarksAwarded() != null;
        dto.setGraded(graded);
        if (graded && dto.getMaxMarks() != null && dto.getMaxMarks().compareTo(BigDecimal.ZERO) > 0) {
            dto.setPercentage(submission.getMarksAwarded()
                    .multiply(new BigDecimal("100"))
                    .divide(dto.getMaxMarks(), 2, RoundingMode.HALF_UP));
        }

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getAssignmentTitle() {
        return assignmentTitle;
    }

    public void setAssignmentTitle(String assignmentTitle) {
        this.assignmentTitle = assignmentTitle;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public BigDecimal getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(BigDecimal maxMarks) {
        this.maxMarks = maxMarks;
    }

    public UserDto getStudent() {
        return student;
    }

    public void setStudent(UserDto student) {
        this.student = student;
    }

    public String getTextResponse() {
        return textResponse;
    }

    public void setTextResponse(String textResponse) {
        this.textResponse = textResponse;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }

    public BigDecimal getMarksAwarded() {
        return marksAwarded;
    }

    public void setMarksAwarded(BigDecimal marksAwarded) {
        this.marksAwarded = marksAwarded;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public LocalDateTime getGradedAt() {
        return gradedAt;
    }

    public void setGradedAt(LocalDateTime gradedAt) {
        this.gradedAt = gradedAt;
    }

    public UserDto getGradedBy() {
        return gradedBy;
    }

    public void setGradedBy(UserDto gradedBy) {
        this.gradedBy = gradedBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public boolean isGraded() {
        return isGraded;
    }

    public void setGraded(boolean graded) {
        isGraded = graded;
    }
}
