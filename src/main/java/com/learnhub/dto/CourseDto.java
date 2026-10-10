package com.learnhub.dto;

import com.learnhub.entity.Course;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CourseDto {

    private Long id;
    private String title;
    private String shortDescription;
    private String description;
    private String category;
    private String difficulty;
    private UserDto instructor;
    private String thumbnailPath;
    private String learningObjectives;
    private String prerequisites;
    private String duration;
    private boolean published;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private int totalModules;
    private int totalLessons;
    private long totalEnrolled;
    private boolean isEnrolled;
    private Double progressPercentage;

    private List<ModuleDto> modules = new ArrayList<>();

    public CourseDto() {
    }

    public static CourseDto fromEntity(Course course, boolean includeModules) {
        if (course == null) return null;
        CourseDto dto = new CourseDto();
        dto.setId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setShortDescription(course.getShortDescription());
        dto.setDescription(course.getDescription());
        dto.setCategory(course.getCategory());
        dto.setDifficulty(course.getDifficulty());
        dto.setInstructor(UserDto.fromEntity(course.getInstructor()));
        dto.setThumbnailPath(course.getThumbnailPath());
        dto.setLearningObjectives(course.getLearningObjectives());
        dto.setPrerequisites(course.getPrerequisites());
        dto.setDuration(course.getDuration());
        dto.setPublished(course.isPublished());
        dto.setCreatedAt(course.getCreatedAt());
        dto.setUpdatedAt(course.getUpdatedAt());

        if (course.getModules() != null) {
            dto.setTotalModules(course.getModules().size());
            int lessonsCount = 0;
            for (var mod : course.getModules()) {
                if (mod.getLessons() != null) {
                    lessonsCount += mod.getLessons().size();
                }
            }
            dto.setTotalLessons(lessonsCount);

            if (includeModules) {
                dto.setModules(course.getModules().stream()
                        .map(ModuleDto::fromEntity)
                        .collect(Collectors.toList()));
            }
        }

        if (course.getEnrollments() != null) {
            dto.setTotalEnrolled(course.getEnrollments().size());
        }

        return dto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public UserDto getInstructor() {
        return instructor;
    }

    public void setInstructor(UserDto instructor) {
        this.instructor = instructor;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getLearningObjectives() {
        return learningObjectives;
    }

    public void setLearningObjectives(String learningObjectives) {
        this.learningObjectives = learningObjectives;
    }

    public String getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(String prerequisites) {
        this.prerequisites = prerequisites;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getTotalModules() {
        return totalModules;
    }

    public void setTotalModules(int totalModules) {
        this.totalModules = totalModules;
    }

    public int getTotalLessons() {
        return totalLessons;
    }

    public void setTotalLessons(int totalLessons) {
        this.totalLessons = totalLessons;
    }

    public long getTotalEnrolled() {
        return totalEnrolled;
    }

    public void setTotalEnrolled(long totalEnrolled) {
        this.totalEnrolled = totalEnrolled;
    }

    public boolean isEnrolled() {
        return isEnrolled;
    }

    public void setEnrolled(boolean enrolled) {
        isEnrolled = enrolled;
    }

    public Double getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(Double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public List<ModuleDto> getModules() {
        return modules;
    }

    public void setModules(List<ModuleDto> modules) {
        this.modules = modules;
    }
}
