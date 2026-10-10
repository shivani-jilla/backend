package com.learnhub.service;

import com.learnhub.dto.LessonCreateRequest;
import com.learnhub.dto.LessonDto;
import com.learnhub.entity.CourseModule;
import com.learnhub.entity.Lesson;
import com.learnhub.exception.ResourceNotFoundException;
import com.learnhub.repository.LessonProgressRepository;
import com.learnhub.repository.LessonRepository;
import com.learnhub.repository.ModuleRepository;
import com.learnhub.security.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;
    private final CourseService courseService;
    private final LessonProgressRepository lessonProgressRepository;

    public LessonService(
            LessonRepository lessonRepository,
            ModuleRepository moduleRepository,
            CourseService courseService,
            LessonProgressRepository lessonProgressRepository
    ) {
        this.lessonRepository = lessonRepository;
        this.moduleRepository = moduleRepository;
        this.courseService = courseService;
        this.lessonProgressRepository = lessonProgressRepository;
    }

    public List<LessonDto> getLessonsByModule(Long moduleId, UserPrincipal principal) {
        List<Lesson> lessons = lessonRepository.findByModuleIdOrderByDisplayOrderAsc(moduleId);
        return lessons.stream()
                .map(lesson -> {
                    LessonDto dto = LessonDto.fromEntity(lesson);
                    if (principal != null) {
                        lessonProgressRepository.findByStudentIdAndLessonId(principal.getId(), lesson.getId())
                                .ifPresent(progress -> dto.setCompleted(progress.isCompleted()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public LessonDto getLessonById(Long lessonId, UserPrincipal principal) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));

        LessonDto dto = LessonDto.fromEntity(lesson);
        if (principal != null) {
            lessonProgressRepository.findByStudentIdAndLessonId(principal.getId(), lesson.getId())
                    .ifPresent(progress -> dto.setCompleted(progress.isCompleted()));
        }
        return dto;
    }

    @Transactional
    public LessonDto createLesson(Long moduleId, LessonCreateRequest request, UserPrincipal principal) {
        CourseModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + moduleId));

        courseService.validateOwnershipOrAdmin(module.getCourse(), principal);

        Lesson lesson = new Lesson();
        lesson.setModule(module);
        lesson.setTitle(request.getTitle().trim());
        lesson.setDescription(request.getDescription());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setLocalVideoPath(request.getLocalVideoPath());
        lesson.setDuration(request.getDuration());
        lesson.setDisplayOrder(request.getDisplayOrder());
        lesson.setResourcePath(request.getResourcePath());
        lesson.setPublished(request.isPublished());

        Lesson saved = lessonRepository.save(lesson);
        return LessonDto.fromEntity(saved);
    }

    @Transactional
    public LessonDto updateLesson(Long lessonId, LessonCreateRequest request, UserPrincipal principal) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));

        courseService.validateOwnershipOrAdmin(lesson.getModule().getCourse(), principal);

        lesson.setTitle(request.getTitle().trim());
        lesson.setDescription(request.getDescription());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setLocalVideoPath(request.getLocalVideoPath());
        lesson.setDuration(request.getDuration());
        lesson.setDisplayOrder(request.getDisplayOrder());
        lesson.setResourcePath(request.getResourcePath());
        lesson.setPublished(request.isPublished());

        Lesson updated = lessonRepository.save(lesson);
        return LessonDto.fromEntity(updated);
    }

    @Transactional
    public void deleteLesson(Long lessonId, UserPrincipal principal) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));

        courseService.validateOwnershipOrAdmin(lesson.getModule().getCourse(), principal);

        lessonRepository.delete(lesson);
    }
}
