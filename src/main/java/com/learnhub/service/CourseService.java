package com.learnhub.service;

import com.learnhub.dto.CourseCreateRequest;
import com.learnhub.dto.CourseDto;
import com.learnhub.entity.Course;
import com.learnhub.entity.Role;
import com.learnhub.entity.User;
import com.learnhub.exception.ResourceNotFoundException;
import com.learnhub.exception.UnauthorizedException;
import com.learnhub.repository.CourseRepository;
import com.learnhub.repository.EnrollmentRepository;
import com.learnhub.repository.LessonProgressRepository;
import com.learnhub.repository.LessonRepository;
import com.learnhub.repository.UserRepository;
import com.learnhub.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final LessonRepository lessonRepository;

    public CourseService(
            CourseRepository courseRepository,
            UserRepository userRepository,
            EnrollmentRepository enrollmentRepository,
            LessonProgressRepository lessonProgressRepository,
            LessonRepository lessonRepository
    ) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.lessonProgressRepository = lessonProgressRepository;
        this.lessonRepository = lessonRepository;
    }

    public Page<CourseDto> getPublishedCourses(String search, String category, String difficulty, Pageable pageable) {
        return courseRepository.searchPublishedCourses(search, category, difficulty, pageable)
                .map(course -> CourseDto.fromEntity(course, false));
    }

    public Page<CourseDto> getAllCoursesAdmin(String search, String category, String difficulty, Boolean published, Pageable pageable) {
        return courseRepository.searchAllCoursesAdmin(search, category, difficulty, published, pageable)
                .map(course -> CourseDto.fromEntity(course, false));
    }

    public Page<CourseDto> getInstructorCourses(UserPrincipal principal, Pageable pageable) {
        return courseRepository.findByInstructorId(principal.getId(), pageable)
                .map(course -> CourseDto.fromEntity(course, false));
    }

    public CourseDto getCourseDetails(Long courseId, UserPrincipal principal) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        CourseDto dto = CourseDto.fromEntity(course, true);

        if (principal != null) {
            boolean isEnrolled = enrollmentRepository.existsByStudentIdAndCourseId(principal.getId(), courseId);
            dto.setEnrolled(isEnrolled);

            if (isEnrolled) {
                long totalPublished = lessonRepository.countByModuleCourseIdAndPublishedTrue(courseId);
                long completed = lessonProgressRepository.countCompletedByStudentAndCourse(principal.getId(), courseId);
                double pct = totalPublished > 0 ? ((double) completed / totalPublished) * 100.0 : 0.0;
                dto.setProgressPercentage(Math.round(pct * 100.0) / 100.0);
            }
        }

        return dto;
    }

    @Transactional
    public CourseDto createCourse(CourseCreateRequest request, UserPrincipal principal) {
        User instructor = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));

        Course course = new Course();
        course.setTitle(request.getTitle().trim());
        course.setShortDescription(request.getShortDescription());
        course.setDescription(request.getDescription());
        course.setCategory(request.getCategory());
        course.setDifficulty(request.getDifficulty());
        course.setInstructor(instructor);
        course.setThumbnailPath(request.getThumbnailPath());
        course.setLearningObjectives(request.getLearningObjectives());
        course.setPrerequisites(request.getPrerequisites());
        course.setDuration(request.getDuration());
        course.setPublished(request.isPublished());

        Course saved = courseRepository.save(course);
        return CourseDto.fromEntity(saved, true);
    }

    @Transactional
    public CourseDto updateCourse(Long courseId, CourseCreateRequest request, UserPrincipal principal) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        validateOwnershipOrAdmin(course, principal);

        course.setTitle(request.getTitle().trim());
        course.setShortDescription(request.getShortDescription());
        course.setDescription(request.getDescription());
        course.setCategory(request.getCategory());
        course.setDifficulty(request.getDifficulty());
        course.setThumbnailPath(request.getThumbnailPath());
        course.setLearningObjectives(request.getLearningObjectives());
        course.setPrerequisites(request.getPrerequisites());
        course.setDuration(request.getDuration());
        course.setPublished(request.isPublished());

        Course updated = courseRepository.save(course);
        return CourseDto.fromEntity(updated, true);
    }

    @Transactional
    public CourseDto togglePublishCourse(Long courseId, UserPrincipal principal) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        validateOwnershipOrAdmin(course, principal);

        course.setPublished(!course.isPublished());
        Course updated = courseRepository.save(course);
        return CourseDto.fromEntity(updated, true);
    }

    @Transactional
    public void deleteCourse(Long courseId, UserPrincipal principal) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        validateOwnershipOrAdmin(course, principal);

        courseRepository.delete(course);
    }

    public void validateOwnershipOrAdmin(Course course, UserPrincipal principal) {
        if (principal.getRole() == Role.ADMIN) {
            return;
        }
        if (!course.getInstructor().getId().equals(principal.getId())) {
            throw new UnauthorizedException("You do not have permission to manage this course");
        }
    }
}
