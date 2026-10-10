package com.learnhub.controller;

import com.learnhub.dto.LessonCreateRequest;
import com.learnhub.dto.LessonDto;
import com.learnhub.security.UserPrincipal;
import com.learnhub.service.LessonProgressService;
import com.learnhub.service.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Lessons", description = "Video lessons and learning progress endpoints")
public class LessonController {

    private final LessonService lessonService;
    private final LessonProgressService lessonProgressService;

    public LessonController(LessonService lessonService, LessonProgressService lessonProgressService) {
        this.lessonService = lessonService;
        this.lessonProgressService = lessonProgressService;
    }

    @GetMapping("/api/modules/{moduleId}/lessons")
    @Operation(summary = "Get lessons for a module")
    public ResponseEntity<List<LessonDto>> getLessons(
            @PathVariable Long moduleId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(lessonService.getLessonsByModule(moduleId, principal));
    }

    @GetMapping("/api/lessons/{lessonId}")
    @Operation(summary = "Get lesson details by id")
    public ResponseEntity<LessonDto> getLesson(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(lessonService.getLessonById(lessonId, principal));
    }

    @PostMapping("/api/modules/{moduleId}/lessons")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @Operation(summary = "Create lesson in a module (Instructor/Admin)")
    public ResponseEntity<LessonDto> createLesson(
            @PathVariable Long moduleId,
            @Valid @RequestBody LessonCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        LessonDto created = lessonService.createLesson(moduleId, request, principal);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/api/lessons/{lessonId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @Operation(summary = "Update lesson (Instructor/Admin)")
    public ResponseEntity<LessonDto> updateLesson(
            @PathVariable Long lessonId,
            @Valid @RequestBody LessonCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(lessonService.updateLesson(lessonId, request, principal));
    }

    @DeleteMapping("/api/lessons/{lessonId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @Operation(summary = "Delete lesson (Instructor/Admin)")
    public ResponseEntity<Void> deleteLesson(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        lessonService.deleteLesson(lessonId, principal);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/lessons/{lessonId}/complete")
    @Operation(summary = "Toggle lesson completed status for current student")
    public ResponseEntity<Map<String, Object>> markLessonComplete(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(lessonProgressService.markLessonComplete(lessonId, principal));
    }

    @PostMapping("/api/lessons/{lessonId}/access")
    @Operation(summary = "Record timestamp when student accesses or resumes a lesson")
    public ResponseEntity<Void> recordLessonAccess(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        lessonProgressService.recordLessonAccess(lessonId, principal);
        return ResponseEntity.ok().build();
    }
}
