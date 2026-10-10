package com.learnhub;

import com.learnhub.dto.GradeRequest;
import com.learnhub.dto.SubmissionCreateRequest;
import com.learnhub.dto.SubmissionDto;
import com.learnhub.entity.*;
import com.learnhub.exception.BusinessException;
import com.learnhub.repository.*;
import com.learnhub.security.UserPrincipal;
import com.learnhub.service.CourseService;
import com.learnhub.service.GradeService;
import com.learnhub.service.SubmissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AssignmentAndGradingTest {

    @Mock
    private SubmissionRepository submissionRepository;
    @Mock
    private AssignmentRepository assignmentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private CourseService courseService;

    private SubmissionService submissionService;
    private GradeService gradeService;

    private User student;
    private User instructor;
    private Course course;
    private Assignment assignment;

    @BeforeEach
    void setUp() {
        submissionService = new SubmissionService(
                submissionRepository, assignmentRepository, userRepository,
                enrollmentRepository, courseService
        );
        gradeService = new GradeService(
                submissionRepository, userRepository, courseRepository, courseService
        );

        student = new User(1L, "Charlie Student", "charlie@learnhub.com", "hash", Role.STUDENT, true);
        instructor = new User(2L, "Prof. Davis", "davis@learnhub.com", "hash", Role.INSTRUCTOR, true);

        course = new Course();
        course.setId(30L);
        course.setTitle("Database Systems");
        course.setInstructor(instructor);

        assignment = new Assignment();
        assignment.setId(300L);
        assignment.setCourse(course);
        assignment.setTitle("SQL Normalization Assignment");
        assignment.setMaxMarks(new BigDecimal("100.00"));
        assignment.setAllowedSubmissionType(SubmissionType.TEXT);
    }

    @Test
    void testLateSubmissionRejectedWhenNotAllowed() {
        assignment.setDueDate(LocalDateTime.now().minusHours(2));
        assignment.setAllowLateSubmissions(false);

        UserPrincipal principal = new UserPrincipal(student);
        SubmissionCreateRequest req = new SubmissionCreateRequest("My SQL script", null, null);

        when(assignmentRepository.findById(300L)).thenReturn(Optional.of(assignment));
        when(enrollmentRepository.existsByStudentIdAndCourseId(1L, 30L)).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                submissionService.submitAssignment(300L, req, principal)
        );
        assertTrue(ex.getMessage().contains("deadline for this assignment has passed"));
        verify(submissionRepository, never()).save(any());
    }

    @Test
    void testLateSubmissionAcceptedWhenAllowed() {
        assignment.setDueDate(LocalDateTime.now().minusHours(2));
        assignment.setAllowLateSubmissions(true);

        UserPrincipal principal = new UserPrincipal(student);
        SubmissionCreateRequest req = new SubmissionCreateRequest("My late SQL script", null, null);

        when(assignmentRepository.findById(300L)).thenReturn(Optional.of(assignment));
        when(enrollmentRepository.existsByStudentIdAndCourseId(1L, 30L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(submissionRepository.findByAssignmentIdAndStudentId(300L, 1L)).thenReturn(Optional.empty());
        when(submissionRepository.save(any(Submission.class))).thenAnswer(i -> {
            Submission s = i.getArgument(0);
            s.setId(500L);
            return s;
        });

        SubmissionDto dto = submissionService.submitAssignment(300L, req, principal);

        assertNotNull(dto);
        assertEquals(SubmissionStatus.LATE, dto.getStatus());
        verify(submissionRepository).save(any(Submission.class));
    }

    @Test
    void testGradeSubmissionNegativeMarksRejected() {
        Submission sub = new Submission();
        sub.setId(500L);
        sub.setAssignment(assignment);
        sub.setStudent(student);

        UserPrincipal principal = new UserPrincipal(instructor);
        GradeRequest req = new GradeRequest(new BigDecimal("-5.00"), "Invalid marks");

        when(submissionRepository.findById(500L)).thenReturn(Optional.of(sub));

        BusinessException ex = assertThrows(BusinessException.class, () ->
                gradeService.gradeSubmission(500L, req, principal)
        );
        assertTrue(ex.getMessage().contains("cannot be negative"));
    }

    @Test
    void testGradeSubmissionExceedingMaxMarksRejected() {
        Submission sub = new Submission();
        sub.setId(500L);
        sub.setAssignment(assignment);
        sub.setStudent(student);

        UserPrincipal principal = new UserPrincipal(instructor);
        GradeRequest req = new GradeRequest(new BigDecimal("105.00"), "Exceeds max");

        when(submissionRepository.findById(500L)).thenReturn(Optional.of(sub));

        BusinessException ex = assertThrows(BusinessException.class, () ->
                gradeService.gradeSubmission(500L, req, principal)
        );
        assertTrue(ex.getMessage().contains("cannot exceed maximum marks"));
    }

    @Test
    void testGradeSubmissionSuccess() {
        Submission sub = new Submission();
        sub.setId(500L);
        sub.setAssignment(assignment);
        sub.setStudent(student);

        UserPrincipal principal = new UserPrincipal(instructor);
        GradeRequest req = new GradeRequest(new BigDecimal("88.50"), "Great work on 3NF normalization!");

        when(submissionRepository.findById(500L)).thenReturn(Optional.of(sub));
        when(userRepository.findById(2L)).thenReturn(Optional.of(instructor));
        when(submissionRepository.save(any(Submission.class))).thenAnswer(i -> i.getArgument(0));

        SubmissionDto dto = gradeService.gradeSubmission(500L, req, principal);

        assertNotNull(dto);
        assertEquals(SubmissionStatus.GRADED, dto.getStatus());
        assertEquals(new BigDecimal("88.50"), dto.getMarksAwarded());
        assertEquals(new BigDecimal("88.50"), dto.getPercentage());
        assertTrue(dto.isGraded());
        verify(submissionRepository).save(sub);
    }
}
