package com.learnhub.dto;

import com.learnhub.entity.Lesson;

public class LessonDto {

    private Long id;
    private Long moduleId;
    private String title;
    private String description;
    private String videoUrl;
    private String localVideoPath;
    private String duration;
    private int displayOrder;
    private String resourcePath;
    private boolean published;
    private boolean completed;

    public LessonDto() {
    }

    public static LessonDto fromEntity(Lesson lesson) {
        if (lesson == null) return null;
        LessonDto dto = new LessonDto();
        dto.setId(lesson.getId());
        if (lesson.getModule() != null) {
            dto.setModuleId(lesson.getModule().getId());
        }
        dto.setTitle(lesson.getTitle());
        dto.setDescription(lesson.getDescription());
        dto.setVideoUrl(lesson.getVideoUrl());
        dto.setLocalVideoPath(lesson.getLocalVideoPath());
        dto.setDuration(lesson.getDuration());
        dto.setDisplayOrder(lesson.getDisplayOrder());
        dto.setResourcePath(lesson.getResourcePath());
        dto.setPublished(lesson.isPublished());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
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

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getLocalVideoPath() {
        return localVideoPath;
    }

    public void setLocalVideoPath(String localVideoPath) {
        this.localVideoPath = localVideoPath;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
