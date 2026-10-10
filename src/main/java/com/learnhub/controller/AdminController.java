package com.learnhub.controller;

import com.learnhub.dto.CourseDto;
import com.learnhub.dto.DashboardStatsDto;
import com.learnhub.dto.UserDto;
import com.learnhub.entity.Role;
import com.learnhub.security.UserPrincipal;
import com.learnhub.service.AdminService;
import com.learnhub.service.CourseService;
import com.learnhub.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Tag(name = "Administration", description = "Platform management and dashboard analytics endpoints")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final CourseService courseService;

    public AdminController(AdminService adminService, UserService userService, CourseService courseService) {
        this.adminService = adminService;
        this.userService = userService;
        this.courseService = courseService;
    }

    @GetMapping("/api/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get platform-wide analytics and statistics (Admin only)")
    public ResponseEntity<DashboardStatsDto> getAdminDashboardStats() {
        return ResponseEntity.ok(adminService.getAdminDashboardStats());
    }

    @GetMapping("/api/dashboard/instructor-stats")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @Operation(summary = "Get instructor dashboard statistics")
    public ResponseEntity<DashboardStatsDto> getInstructorStats(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(adminService.getInstructorDashboardStats(principal));
    }

    @GetMapping("/api/dashboard/student-stats")
    @Operation(summary = "Get student dashboard statistics")
    public ResponseEntity<DashboardStatsDto> getStudentStats(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(adminService.getStudentDashboardStats(principal));
    }

    @GetMapping("/api/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Search and list all users with role filter and pagination (Admin only)")
    public ResponseEntity<Page<UserDto>> listUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(userService.searchUsers(role, search, pageable));
    }

    @PutMapping("/api/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user details or assign role (Admin only)")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload
    ) {
        String name = (String) payload.get("name");
        String roleStr = (String) payload.get("role");
        Role role = roleStr != null ? Role.valueOf(roleStr) : null;
        Boolean active = (Boolean) payload.get("active");

        return ResponseEntity.ok(userService.updateUser(id, name, role, active));
    }

    @PatchMapping("/api/admin/users/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate or deactivate a user account (Admin only)")
    public ResponseEntity<UserDto> toggleUserStatus(@PathVariable Long id) {
        return ResponseEntity.ok(userService.toggleUserStatus(id));
    }

    @DeleteMapping("/api/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user account (Admin only)")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/admin/courses")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List and search all courses including unpublished (Admin only)")
    public ResponseEntity<Page<CourseDto>> listAllCoursesAdmin(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) Boolean published,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(courseService.getAllCoursesAdmin(search, category, difficulty, published, pageable));
    }
}
