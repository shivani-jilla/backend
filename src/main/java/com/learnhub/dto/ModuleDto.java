package com.learnhub.dto;

import com.learnhub.entity.CourseModule;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleDto {

    private Long id;
    private Long courseId;
    private String title;
    private String description;
    private int displayOrder;
    private List<LessonDto> lessons = new ArrayList<>();

    public ModuleDto() {
    }

    public static ModuleDto fromEntity(CourseModule module) {
        if (module == null) return null;
        ModuleDto dto = new ModuleDto();
        dto.setId(module.getId());
        if (module.getCourse() != null) {
            dto.setCourseId(module.getCourse().getId());
        }
        dto.setTitle(module.getTitle());
        dto.setDescription(module.getDescription());
        dto.setDisplayOrder(module.getDisplayOrder());
        if (module.getLessons() != null) {
            dto.setLessons(module.getLessons().stream()
                    .map(LessonDto::fromEntity)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
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

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public List<LessonDto> getLessons() {
        return lessons;
    }

    public void setLessons(List<LessonDto> lessons) {
        this.lessons = lessons;
    }
}
