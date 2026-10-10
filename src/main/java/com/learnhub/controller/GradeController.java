package com.learnhub.controller;

import com.learnhub.dto.GradeRequest;
import com.learnhub.dto.SubmissionDto;
import com.learnhub.security.UserPrincipal;
import com.learnhub.service.GradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Grading", description = "Grading submissions and gradebook endpoints")
public class GradeController {

    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @PostMapping("/api/submissions/{submissionId}/grade")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @Operation(summary = "Grade student submission with marks and written feedback (Instructor/Admin)")
    public ResponseEntity<SubmissionDto> gradeSubmission(
            @PathVariable Long submissionId,
            @Valid @RequestBody GradeRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(gradeService.gradeSubmission(submissionId, request, principal));
    }

    @GetMapping("/api/grades/my")
    @Operation(summary = "Get student's own grades across enrolled courses")
    public ResponseEntity<List<SubmissionDto>> getMyGrades(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(gradeService.getMyGrades(principal));
    }

    @GetMapping("/api/courses/{courseId}/gradebook")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @Operation(summary = "Get complete gradebook for course assignments (Instructor/Admin)")
    public ResponseEntity<List<SubmissionDto>> getCourseGradebook(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(gradeService.getCourseGradebook(courseId, principal));
    }
}
