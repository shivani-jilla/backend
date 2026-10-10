package com.learnhub.service;

import com.learnhub.entity.Lesson;
import com.learnhub.entity.LessonProgress;
import com.learnhub.entity.User;
import com.learnhub.exception.BusinessException;
import com.learnhub.exception.ResourceNotFoundException;
import com.learnhub.repository.EnrollmentRepository;
import com.learnhub.repository.LessonProgressRepository;
import com.learnhub.repository.LessonRepository;
import com.learnhub.repository.UserRepository;
import com.learnhub.security.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class LessonProgressService {

    private final LessonProgressRepository lessonProgressRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    public LessonProgressService(
            LessonProgressRepository lessonProgressRepository,
            LessonRepository lessonRepository,
            UserRepository userRepository,
            EnrollmentRepository enrollmentRepository
    ) {
        this.lessonProgressRepository = lessonProgressRepository;
        this.lessonRepository = lessonRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Transactional
    public Map<String, Object> markLessonComplete(Long lessonId, UserPrincipal principal) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));

        Long courseId = lesson.getModule().getCourse().getId();

        if (!enrollmentRepository.existsByStudentIdAndCourseId(principal.getId(), courseId)) {
            throw new BusinessException("You must be enrolled in the course to complete lessons");
        }

        User student = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LessonProgress progress = lessonProgressRepository
                .findByStudentIdAndLessonId(student.getId(), lesson.getId())
                .orElseGet(() -> new LessonProgress(student, lesson));

        boolean wasCompleted = progress.isCompleted();
        progress.setCompleted(!wasCompleted);
        progress.setLastAccessedAt(LocalDateTime.now());
        if (!wasCompleted) {
            progress.setCompletedAt(LocalDateTime.now());
        } else {
            progress.setCompletedAt(null);
        }

        lessonProgressRepository.save(progress);

        long totalLessons = lessonRepository.countByModuleCourseIdAndPublishedTrue(courseId);
        long completedLessons = lessonProgressRepository.countCompletedByStudentAndCourse(student.getId(), courseId);
        double progressPercentage = totalLessons > 0 ? ((double) completedLessons / totalLessons) * 100.0 : 0.0;

        Map<String, Object> response = new HashMap<>();
        response.put("lessonId", lessonId);
        response.put("completed", progress.isCompleted());
        response.put("totalLessons", totalLessons);
        response.put("completedLessons", completedLessons);
        response.put("progressPercentage", Math.round(progressPercentage * 100.0) / 100.0);

        return response;
    }

    @Transactional
    public void recordLessonAccess(Long lessonId, UserPrincipal principal) {
        if (principal == null) return;

        lessonRepository.findById(lessonId).ifPresent(lesson -> {
            Long courseId = lesson.getModule().getCourse().getId();
            if (enrollmentRepository.existsByStudentIdAndCourseId(principal.getId(), courseId)) {
                userRepository.findById(principal.getId()).ifPresent(student -> {
                    LessonProgress progress = lessonProgressRepository
                            .findByStudentIdAndLessonId(student.getId(), lesson.getId())
                            .orElseGet(() -> new LessonProgress(student, lesson));
                    progress.setLastAccessedAt(LocalDateTime.now());
                    lessonProgressRepository.save(progress);
                });
            }
        });
    }

    public Map<String, Object> getCourseProgress(Long courseId, UserPrincipal principal) {
        long totalLessons = lessonRepository.countByModuleCourseIdAndPublishedTrue(courseId);
        long completedLessons = lessonProgressRepository.countCompletedByStudentAndCourse(principal.getId(), courseId);
        double progressPercentage = totalLessons > 0 ? ((double) completedLessons / totalLessons) * 100.0 : 0.0;

        Map<String, Object> response = new HashMap<>();
        response.put("courseId", courseId);
        response.put("totalLessons", totalLessons);
        response.put("completedLessons", completedLessons);
        response.put("progressPercentage", Math.round(progressPercentage * 100.0) / 100.0);
        return response;
    }
}
