package com.learnhub.service;

import com.learnhub.dto.ModuleCreateRequest;
import com.learnhub.dto.ModuleDto;
import com.learnhub.entity.Course;
import com.learnhub.entity.CourseModule;
import com.learnhub.exception.ResourceNotFoundException;
import com.learnhub.repository.CourseRepository;
import com.learnhub.repository.ModuleRepository;
import com.learnhub.security.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;
    private final CourseService courseService;

    public ModuleService(
            ModuleRepository moduleRepository,
            CourseRepository courseRepository,
            CourseService courseService
    ) {
        this.moduleRepository = moduleRepository;
        this.courseRepository = courseRepository;
        this.courseService = courseService;
    }

    public List<ModuleDto> getModulesByCourse(Long courseId) {
        return moduleRepository.findByCourseIdOrderByDisplayOrderAsc(courseId).stream()
                .map(ModuleDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ModuleDto createModule(Long courseId, ModuleCreateRequest request, UserPrincipal principal) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        courseService.validateOwnershipOrAdmin(course, principal);

        CourseModule module = new CourseModule();
        module.setCourse(course);
        module.setTitle(request.getTitle().trim());
        module.setDescription(request.getDescription());
        module.setDisplayOrder(request.getDisplayOrder());

        CourseModule saved = moduleRepository.save(module);
        return ModuleDto.fromEntity(saved);
    }

    @Transactional
    public ModuleDto updateModule(Long moduleId, ModuleCreateRequest request, UserPrincipal principal) {
        CourseModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + moduleId));

        courseService.validateOwnershipOrAdmin(module.getCourse(), principal);

        module.setTitle(request.getTitle().trim());
        module.setDescription(request.getDescription());
        module.setDisplayOrder(request.getDisplayOrder());

        CourseModule updated = moduleRepository.save(module);
        return ModuleDto.fromEntity(updated);
    }

    @Transactional
    public void deleteModule(Long moduleId, UserPrincipal principal) {
        CourseModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + moduleId));

        courseService.validateOwnershipOrAdmin(module.getCourse(), principal);

        moduleRepository.delete(module);
    }
}
