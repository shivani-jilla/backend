package com.learnhub.controller;

import com.learnhub.dto.ModuleCreateRequest;
import com.learnhub.dto.ModuleDto;
import com.learnhub.security.UserPrincipal;
import com.learnhub.service.ModuleService;
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
@Tag(name = "Modules", description = "Course module structure endpoints")
public class ModuleController {

    private final ModuleService moduleService;

    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @GetMapping("/api/courses/{courseId}/modules")
    @Operation(summary = "Get all modules for a course")
    public ResponseEntity<List<ModuleDto>> getModulesByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(moduleService.getModulesByCourse(courseId));
    }

    @PostMapping("/api/courses/{courseId}/modules")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @Operation(summary = "Create module in a course (Instructor/Admin)")
    public ResponseEntity<ModuleDto> createModule(
            @PathVariable Long courseId,
            @Valid @RequestBody ModuleCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ModuleDto created = moduleService.createModule(courseId, request, principal);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/api/modules/{moduleId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @Operation(summary = "Update module (Instructor/Admin)")
    public ResponseEntity<ModuleDto> updateModule(
            @PathVariable Long moduleId,
            @Valid @RequestBody ModuleCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(moduleService.updateModule(moduleId, request, principal));
    }

    @DeleteMapping("/api/modules/{moduleId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @Operation(summary = "Delete module (Instructor/Admin)")
    public ResponseEntity<Void> deleteModule(
            @PathVariable Long moduleId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        moduleService.deleteModule(moduleId, principal);
        return ResponseEntity.noContent().build();
    }
}
