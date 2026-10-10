package com.learnhub.controller;

import com.learnhub.dto.EnrollmentDto;
import com.learnhub.security.UserPrincipal;
import com.learnhub.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Enrollments", description = "Course enrollment and student progress endpoints")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping("/api/courses/{courseId}/enroll")
    @Operation(summary = "Enroll current student in a published course")
    public ResponseEntity<EnrollmentDto> enroll(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        EnrollmentDto enrolled = enrollmentService.enroll(courseId, principal);
        return new ResponseEntity<>(enrolled, HttpStatus.CREATED);
    }

    @GetMapping("/api/enrollments/my")
    @Operation(summary = "Get enrolled courses for current student with progress statistics")
    public ResponseEntity<Page<EnrollmentDto>> getMyEnrollments(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("enrolledAt").descending());
        return ResponseEntity.ok(enrollmentService.getMyEnrollments(principal, pageable));
    }

    @GetMapping("/api/courses/{courseId}/students")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @Operation(summary = "Get list of enrolled students for a course (Instructor/Admin)")
    public ResponseEntity<Page<EnrollmentDto>> getCourseStudents(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("enrolledAt").descending());
        return ResponseEntity.ok(enrollmentService.getCourseEnrollments(courseId, principal, pageable));
    }
}
