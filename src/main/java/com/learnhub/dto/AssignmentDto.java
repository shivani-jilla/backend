package com.learnhub.dto;

import com.learnhub.entity.Assignment;
import com.learnhub.entity.SubmissionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AssignmentDto {

    private Long id;
    private Long courseId;
    private String courseTitle;
    private String title;
    private String description;
    private String instructions;
    private BigDecimal maxMarks;
    private LocalDateTime dueDate;
    private SubmissionType allowedSubmissionType;
    private String attachmentPath;
    private boolean allowLateSubmissions;
    private boolean published;
    private LocalDateTime createdAt;
    private boolean isPastDue;
    private boolean hasSubmitted;
    private SubmissionDto mySubmission;
    private int totalSubmissions;

    public AssignmentDto() {
    }

    public static AssignmentDto fromEntity(Assignment assignment) {
        if (assignment == null) return null;
        AssignmentDto dto = new AssignmentDto();
        dto.setId(assignment.getId());
        if (assignment.getCourse() != null) {
            dto.setCourseId(assignment.getCourse().getId());
            dto.setCourseTitle(assignment.getCourse().getTitle());
        }
        dto.setTitle(assignment.getTitle());
        dto.setDescription(assignment.getDescription());
        dto.setInstructions(assignment.getInstructions());
        dto.setMaxMarks(assignment.getMaxMarks());
        dto.setDueDate(assignment.getDueDate());
        dto.setAllowedSubmissionType(assignment.getAllowedSubmissionType());
        dto.setAttachmentPath(assignment.getAttachmentPath());
        dto.setAllowLateSubmissions(assignment.isAllowLateSubmissions());
        dto.setPublished(assignment.isPublished());
        dto.setCreatedAt(assignment.getCreatedAt());
        if (assignment.getDueDate() != null) {
            dto.setPastDue(LocalDateTime.now().isAfter(assignment.getDueDate()));
        }
        if (assignment.getSubmissions() != null) {
            dto.setTotalSubmissions(assignment.getSubmissions().size());
        }
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public BigDecimal getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(BigDecimal maxMarks) {
        this.maxMarks = maxMarks;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public SubmissionType getAllowedSubmissionType() {
        return allowedSubmissionType;
    }

    public void setAllowedSubmissionType(SubmissionType allowedSubmissionType) {
        this.allowedSubmissionType = allowedSubmissionType;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    public boolean isAllowLateSubmissions() {
        return allowLateSubmissions;
    }

    public void setAllowLateSubmissions(boolean allowLateSubmissions) {
        this.allowLateSubmissions = allowLateSubmissions;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isPastDue() {
        return isPastDue;
    }

    public void setPastDue(boolean pastDue) {
        isPastDue = pastDue;
    }

    public boolean isHasSubmitted() {
        return hasSubmitted;
    }

    public void setHasSubmitted(boolean hasSubmitted) {
        this.hasSubmitted = hasSubmitted;
    }

    public SubmissionDto getMySubmission() {
        return mySubmission;
    }

    public void setMySubmission(SubmissionDto mySubmission) {
        this.mySubmission = mySubmission;
    }

    public int getTotalSubmissions() {
        return totalSubmissions;
    }

    public void setTotalSubmissions(int totalSubmissions) {
        this.totalSubmissions = totalSubmissions;
    }
}
