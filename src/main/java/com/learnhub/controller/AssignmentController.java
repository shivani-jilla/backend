package com.learnhub.controller;

import com.learnhub.dto.AssignmentCreateRequest;
import com.learnhub.dto.AssignmentDto;
import com.learnhub.security.UserPrincipal;
import com.learnhub.service.AssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Assignments", description = "Course assignments management")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping("/api/courses/{courseId}/assignments")
    @Operation(summary = "Get assignments for a course")
    public ResponseEntity<List<AssignmentDto>> getAssignments(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(assignmentService.getAssignmentsByCourse(courseId, principal));
    }

    @GetMapping("/api/assignments/{assignmentId}")
    @Operation(summary = "Get assignment details by id")
    public ResponseEntity<AssignmentDto> getAssignment(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(assignmentService.getAssignmentById(assignmentId, principal));
    }

    @PostMapping("/api/courses/{courseId}/assignments")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @Operation(summary = "Create an assignment for a course (Instructor/Admin)")
    public ResponseEntity<AssignmentDto> createAssignment(
            @PathVariable Long courseId,
            @Valid @RequestBody AssignmentCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        AssignmentDto created = assignmentService.createAssignment(courseId, request, principal);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/api/assignments/{assignmentId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @Operation(summary = "Update an assignment (Instructor/Admin)")
    public ResponseEntity<AssignmentDto> updateAssignment(
            @PathVariable Long assignmentId,
            @Valid @RequestBody AssignmentCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(assignmentService.updateAssignment(assignmentId, request, principal));
    }

    @DeleteMapping("/api/assignments/{assignmentId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @Operation(summary = "Delete an assignment (Instructor/Admin)")
    public ResponseEntity<Void> deleteAssignment(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        assignmentService.deleteAssignment(assignmentId, principal);
        return ResponseEntity.noContent().build();
    }
}
