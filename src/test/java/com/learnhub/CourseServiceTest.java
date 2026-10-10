package com.learnhub;

import com.learnhub.dto.CourseCreateRequest;
import com.learnhub.dto.CourseDto;
import com.learnhub.entity.Course;
import com.learnhub.entity.Role;
import com.learnhub.entity.User;
import com.learnhub.exception.UnauthorizedException;
import com.learnhub.repository.CourseRepository;
import com.learnhub.repository.EnrollmentRepository;
import com.learnhub.repository.LessonProgressRepository;
import com.learnhub.repository.LessonRepository;
import com.learnhub.repository.UserRepository;
import com.learnhub.security.UserPrincipal;
import com.learnhub.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private LessonProgressRepository lessonProgressRepository;
    @Mock
    private LessonRepository lessonRepository;

    @InjectMocks
    private CourseService courseService;

    private User instructor;
    private User anotherInstructor;
    private Course sampleCourse;

    @BeforeEach
    void setUp() {
        instructor = new User(10L, "Dr. Instructor", "dr@learnhub.com", "hash", Role.INSTRUCTOR, true);
        anotherInstructor = new User(20L, "Other Instructor", "other@learnhub.com", "hash", Role.INSTRUCTOR, true);

        sampleCourse = new Course();
        sampleCourse.setId(100L);
        sampleCourse.setTitle("Modern Java Development");
        sampleCourse.setCategory("Programming");
        sampleCourse.setDifficulty("Intermediate");
        sampleCourse.setInstructor(instructor);
        sampleCourse.setPublished(false);
    }

    @Test
    void testCreateCourseSuccess() {
        CourseCreateRequest req = new CourseCreateRequest();
        req.setTitle("Modern Java Development");
        req.setCategory("Programming");
        req.setDifficulty("Intermediate");
        req.setPublished(true);

        UserPrincipal principal = new UserPrincipal(instructor);
        when(userRepository.findById(10L)).thenReturn(Optional.of(instructor));
        when(courseRepository.save(any(Course.class))).thenAnswer(i -> {
            Course c = i.getArgument(0);
            c.setId(100L);
            return c;
        });

        CourseDto dto = courseService.createCourse(req, principal);

        assertNotNull(dto);
        assertEquals("Modern Java Development", dto.getTitle());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void testUnauthorizedCourseModificationRejected() {
        CourseCreateRequest updateReq = new CourseCreateRequest();
        updateReq.setTitle("Malicious Title");
        updateReq.setCategory("Programming");
        updateReq.setDifficulty("Intermediate");

        UserPrincipal unauthorizedPrincipal = new UserPrincipal(anotherInstructor);
        when(courseRepository.findById(100L)).thenReturn(Optional.of(sampleCourse));

        assertThrows(UnauthorizedException.class, () ->
                courseService.updateCourse(100L, updateReq, unauthorizedPrincipal)
        );
        verify(courseRepository, never()).save(any());
    }

    @Test
    void testTogglePublishCourse() {
        UserPrincipal principal = new UserPrincipal(instructor);
        when(courseRepository.findById(100L)).thenReturn(Optional.of(sampleCourse));
        when(courseRepository.save(any(Course.class))).thenAnswer(i -> i.getArgument(0));

        CourseDto dto = courseService.togglePublishCourse(100L, principal);

        assertTrue(dto.isPublished());
        verify(courseRepository).save(sampleCourse);
    }
}
