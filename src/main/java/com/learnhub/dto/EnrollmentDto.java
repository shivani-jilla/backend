package com.learnhub.dto;

import com.learnhub.entity.Enrollment;
import com.learnhub.entity.EnrollmentStatus;
import java.time.LocalDateTime;

public class EnrollmentDto {

    private Long id;
    private UserDto student;
    private CourseDto course;
    private LocalDateTime enrolledAt;
    private EnrollmentStatus status;
    private double progressPercentage;
    private long completedLessons;
    private long totalLessons;
    private Long lastAccessedLessonId;

    public EnrollmentDto() {
    }

    public static EnrollmentDto fromEntity(Enrollment enrollment) {
        if (enrollment == null) return null;
        EnrollmentDto dto = new EnrollmentDto();
        dto.setId(enrollment.getId());
        dto.setStudent(UserDto.fromEntity(enrollment.getStudent()));
        dto.setCourse(CourseDto.fromEntity(enrollment.getCourse(), false));
        dto.setEnrolledAt(enrollment.getEnrolledAt());
        dto.setStatus(enrollment.getStatus());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDto getStudent() {
        return student;
    }

    public void setStudent(UserDto student) {
        this.student = student;
    }

    public CourseDto getCourse() {
        return course;
    }

    public void setCourse(CourseDto course) {
        this.course = course;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(LocalDateTime enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public double getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public long getCompletedLessons() {
        return completedLessons;
    }

    public void setCompletedLessons(long completedLessons) {
        this.completedLessons = completedLessons;
    }

    public long getTotalLessons() {
        return totalLessons;
    }

    public void setTotalLessons(long totalLessons) {
        this.totalLessons = totalLessons;
    }

    public Long getLastAccessedLessonId() {
        return lastAccessedLessonId;
    }

    public void setLastAccessedLessonId(Long lastAccessedLessonId) {
        this.lastAccessedLessonId = lastAccessedLessonId;
    }
}
