package com.learnhub.controller;

import com.learnhub.dto.SubmissionCreateRequest;
import com.learnhub.dto.SubmissionDto;
import com.learnhub.security.UserPrincipal;
import com.learnhub.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Submissions", description = "Student assignment submissions endpoints")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping("/api/assignments/{assignmentId}/submissions")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Submit or resubmit assignment response (Student)")
    public ResponseEntity<SubmissionDto> submitAssignment(
            @PathVariable Long assignmentId,
            @RequestBody SubmissionCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        SubmissionDto created = submissionService.submitAssignment(assignmentId, request, principal);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/api/assignments/{assignmentId}/my-submission")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get current student's submission for an assignment")
    public ResponseEntity<SubmissionDto> getMySubmission(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(submissionService.getMySubmission(assignmentId, principal));
    }

    @GetMapping("/api/assignments/{assignmentId}/submissions")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @Operation(summary = "Get all submissions for an assignment (Instructor/Admin)")
    public ResponseEntity<List<SubmissionDto>> getSubmissions(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(submissionService.getSubmissionsByAssignment(assignmentId, principal));
    }

    @GetMapping("/api/submissions/{submissionId}")
    @Operation(summary = "Get specific submission details (Student owner, Instructor, or Admin)")
    public ResponseEntity<SubmissionDto> getSubmissionById(
            @PathVariable Long submissionId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(submissionService.getSubmissionById(submissionId, principal));
    }
}
