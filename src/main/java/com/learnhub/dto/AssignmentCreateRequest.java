package com.learnhub.dto;

import com.learnhub.entity.SubmissionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AssignmentCreateRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    private String description;
    private String instructions;

    @NotNull(message = "Maximum marks is required")
    @DecimalMin(value = "1.00", message = "Maximum marks must be at least 1")
    private BigDecimal maxMarks = new BigDecimal("100.00");

    @NotNull(message = "Due date is required")
    private LocalDateTime dueDate;

    private SubmissionType allowedSubmissionType = SubmissionType.BOTH;
    private String attachmentPath;
    private boolean allowLateSubmissions = true;
    private boolean published = true;

    public AssignmentCreateRequest() {
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
}
