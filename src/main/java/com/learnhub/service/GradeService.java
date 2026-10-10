package com.learnhub.service;

import com.learnhub.dto.GradeRequest;
import com.learnhub.dto.SubmissionDto;
import com.learnhub.entity.Role;
import com.learnhub.entity.Submission;
import com.learnhub.entity.SubmissionStatus;
import com.learnhub.entity.User;
import com.learnhub.exception.BusinessException;
import com.learnhub.exception.ResourceNotFoundException;
import com.learnhub.exception.UnauthorizedException;
import com.learnhub.repository.CourseRepository;
import com.learnhub.repository.SubmissionRepository;
import com.learnhub.repository.UserRepository;
import com.learnhub.security.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GradeService {

    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CourseService courseService;

    public GradeService(
            SubmissionRepository submissionRepository,
            UserRepository userRepository,
            CourseRepository courseRepository,
            CourseService courseService
    ) {
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.courseService = courseService;
    }

    @Transactional
    public SubmissionDto gradeSubmission(Long submissionId, GradeRequest request, UserPrincipal principal) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with id: " + submissionId));

        // 1. Role and authorization check: Only the course instructor or admin can grade
        courseService.validateOwnershipOrAdmin(submission.getAssignment().getCourse(), principal);

        // 2. Validate marks
        BigDecimal maxMarks = submission.getAssignment().getMaxMarks();
        BigDecimal marksAwarded = request.getMarksAwarded();

        if (marksAwarded == null) {
            throw new BusinessException("Marks awarded cannot be null");
        }
        if (marksAwarded.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Marks awarded cannot be negative");
        }
        if (marksAwarded.compareTo(maxMarks) > 0) {
            throw new BusinessException("Marks awarded (" + marksAwarded + ") cannot exceed maximum marks (" + maxMarks + ")");
        }

        User grader = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        submission.setMarksAwarded(marksAwarded);
        submission.setFeedback(request.getFeedback());
        submission.setStatus(SubmissionStatus.GRADED);
        submission.setGradedAt(LocalDateTime.now());
        submission.setGradedBy(grader);

        Submission saved = submissionRepository.save(submission);
        return SubmissionDto.fromEntity(saved);
    }

    public List<SubmissionDto> getMyGrades(UserPrincipal principal) {
        // Return only the student's own submissions
        return submissionRepository.findByStudentId(principal.getId()).stream()
                .map(SubmissionDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<SubmissionDto> getCourseGradebook(Long courseId, UserPrincipal principal) {
        var course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        courseService.validateOwnershipOrAdmin(course, principal);

        return submissionRepository.findByCourseId(courseId).stream()
                .map(SubmissionDto::fromEntity)
                .collect(Collectors.toList());
    }
}
