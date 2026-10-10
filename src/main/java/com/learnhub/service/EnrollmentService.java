package com.learnhub.service;

import com.learnhub.dto.EnrollmentDto;
import com.learnhub.entity.Course;
import com.learnhub.entity.Enrollment;
import com.learnhub.entity.EnrollmentStatus;
import com.learnhub.entity.LessonProgress;
import com.learnhub.entity.User;
import com.learnhub.exception.BusinessException;
import com.learnhub.exception.ResourceNotFoundException;
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

import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final CourseService courseService;

    public EnrollmentService(
            EnrollmentRepository enrollmentRepository,
            CourseRepository courseRepository,
            UserRepository userRepository,
            LessonRepository lessonRepository,
            LessonProgressRepository lessonProgressRepository,
            CourseService courseService
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.lessonRepository = lessonRepository;
        this.lessonProgressRepository = lessonProgressRepository;
        this.courseService = courseService;
    }

    @Transactional
    public EnrollmentDto enroll(Long courseId, UserPrincipal principal) {
        User student = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        if (!course.isPublished()) {
            throw new BusinessException("Cannot enroll in an unpublished course");
        }

        if (enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), course.getId())) {
            throw new BusinessException("You are already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment(student, course);
        Enrollment saved = enrollmentRepository.save(enrollment);

        EnrollmentDto dto = EnrollmentDto.fromEntity(saved);
        populateProgressStats(dto, student.getId(), course.getId());
        return dto;
    }

    public Page<EnrollmentDto> getMyEnrollments(UserPrincipal principal, Pageable pageable) {
        return enrollmentRepository.findByStudentId(principal.getId(), pageable)
                .map(enrollment -> {
                    EnrollmentDto dto = EnrollmentDto.fromEntity(enrollment);
                    populateProgressStats(dto, principal.getId(), enrollment.getCourse().getId());
                    return dto;
                });
    }

    public Page<EnrollmentDto> getCourseEnrollments(Long courseId, UserPrincipal principal, Pageable pageable) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        courseService.validateOwnershipOrAdmin(course, principal);

        return enrollmentRepository.findByCourseId(courseId, pageable)
                .map(enrollment -> {
                    EnrollmentDto dto = EnrollmentDto.fromEntity(enrollment);
                    populateProgressStats(dto, enrollment.getStudent().getId(), courseId);
                    return dto;
                });
    }

    public boolean isStudentEnrolled(Long studentId, Long courseId) {
        return enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId);
    }

    private void populateProgressStats(EnrollmentDto dto, Long studentId, Long courseId) {
        long totalLessons = lessonRepository.countByModuleCourseIdAndPublishedTrue(courseId);
        long completedLessons = lessonProgressRepository.countCompletedByStudentAndCourse(studentId, courseId);
        dto.setTotalLessons(totalLessons);
        dto.setCompletedLessons(completedLessons);

        double pct = totalLessons > 0 ? ((double) completedLessons / totalLessons) * 100.0 : 0.0;
        dto.setProgressPercentage(Math.round(pct * 100.0) / 100.0);

        List<LessonProgress> latest = lessonProgressRepository.findLatestByStudentAndCourse(studentId, courseId);
        if (!latest.isEmpty() && latest.get(0).getLesson() != null) {
            dto.setLastAccessedLessonId(latest.get(0).getLesson().getId());
        }
    }
}
