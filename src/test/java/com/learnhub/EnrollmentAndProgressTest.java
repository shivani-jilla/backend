package com.learnhub;

import com.learnhub.dto.EnrollmentDto;
import com.learnhub.entity.*;
import com.learnhub.exception.BusinessException;
import com.learnhub.repository.*;
import com.learnhub.security.UserPrincipal;
import com.learnhub.service.CourseService;
import com.learnhub.service.EnrollmentService;
import com.learnhub.service.LessonProgressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EnrollmentAndProgressTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private LessonRepository lessonRepository;
    @Mock
    private LessonProgressRepository lessonProgressRepository;
    @Mock
    private CourseService courseService;

    private EnrollmentService enrollmentService;
    private LessonProgressService lessonProgressService;

    private User student;
    private Course course;

    @BeforeEach
    void setUp() {
        enrollmentService = new EnrollmentService(
                enrollmentRepository, courseRepository, userRepository,
                lessonRepository, lessonProgressRepository, courseService
        );
        lessonProgressService = new LessonProgressService(
                lessonProgressRepository, lessonRepository, userRepository, enrollmentRepository
        );

        student = new User(1L, "Bob Student", "bob@example.com", "hash", Role.STUDENT, true);
        course = new Course();
        course.setId(50L);
        course.setTitle("Cloud Computing");
        course.setPublished(true);
    }

    @Test
    void testEnrollSuccess() {
        UserPrincipal principal = new UserPrincipal(student);

        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(50L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseId(1L, 50L)).thenReturn(false);
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(i -> {
            Enrollment e = i.getArgument(0);
            e.setId(10L);
            return e;
        });

        EnrollmentDto dto = enrollmentService.enroll(50L, principal);

        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        verify(enrollmentRepository).save(any(Enrollment.class));
    }

    @Test
    void testDuplicateEnrollmentPrevented() {
        UserPrincipal principal = new UserPrincipal(student);

        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(50L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseId(1L, 50L)).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                enrollmentService.enroll(50L, principal)
        );
        assertTrue(ex.getMessage().contains("already enrolled"));
        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void testLessonCompletionProgressCalculation() {
        CourseModule module = new CourseModule();
        module.setCourse(course);

        Lesson lesson = new Lesson();
        lesson.setId(201L);
        lesson.setModule(module);

        UserPrincipal principal = new UserPrincipal(student);

        when(lessonRepository.findById(201L)).thenReturn(Optional.of(lesson));
        when(enrollmentRepository.existsByStudentIdAndCourseId(1L, 50L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(lessonProgressRepository.findByStudentIdAndLessonId(1L, 201L)).thenReturn(Optional.empty());
        when(lessonRepository.countByModuleCourseIdAndPublishedTrue(50L)).thenReturn(4L);
        when(lessonProgressRepository.countCompletedByStudentAndCourse(1L, 50L)).thenReturn(1L);

        var result = lessonProgressService.markLessonComplete(201L, principal);

        assertNotNull(result);
        assertEquals(true, result.get("completed"));
        assertEquals(4L, result.get("totalLessons"));
        assertEquals(1L, result.get("completedLessons"));
        assertEquals(25.0, result.get("progressPercentage"));
    }
}
