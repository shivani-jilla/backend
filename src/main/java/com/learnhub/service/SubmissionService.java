package com.learnhub.service;

import com.learnhub.dto.SubmissionCreateRequest;
import com.learnhub.dto.SubmissionDto;
import com.learnhub.entity.Assignment;
import com.learnhub.entity.Role;
import com.learnhub.entity.Submission;
import com.learnhub.entity.SubmissionStatus;
import com.learnhub.entity.SubmissionType;
import com.learnhub.entity.User;
import com.learnhub.exception.BusinessException;
import com.learnhub.exception.ResourceNotFoundException;
import com.learnhub.exception.UnauthorizedException;
import com.learnhub.repository.AssignmentRepository;
import com.learnhub.repository.EnrollmentRepository;
import com.learnhub.repository.SubmissionRepository;
import com.learnhub.repository.UserRepository;
import com.learnhub.security.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseService courseService;

    public SubmissionService(
            SubmissionRepository submissionRepository,
            AssignmentRepository assignmentRepository,
            UserRepository userRepository,
            EnrollmentRepository enrollmentRepository,
            CourseService courseService
    ) {
        this.submissionRepository = submissionRepository;
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.courseService = courseService;
    }

    @Transactional
    public SubmissionDto submitAssignment(Long assignmentId, SubmissionCreateRequest request, UserPrincipal principal) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + assignmentId));

        Long courseId = assignment.getCourse().getId();

        // 1. Verify student enrollment
        if (!enrollmentRepository.existsByStudentIdAndCourseId(principal.getId(), courseId)) {
            throw new BusinessException("You must be enrolled in the course to submit this assignment");
        }

        // 2. Check submission type rules
        boolean hasText = request.getTextResponse() != null && !request.getTextResponse().trim().isEmpty();
        boolean hasFile = request.getFilePath() != null && !request.getFilePath().trim().isEmpty();

        if (assignment.getAllowedSubmissionType() == SubmissionType.TEXT && !hasText) {
            throw new BusinessException("A text response is required for this assignment");
        }
        if (assignment.getAllowedSubmissionType() == SubmissionType.FILE && !hasFile) {
            throw new BusinessException("A file upload is required for this assignment");
        }
        if (assignment.getAllowedSubmissionType() == SubmissionType.BOTH && !hasText && !hasFile) {
            throw new BusinessException("Please provide a text response or upload a file");
        }

        // 3. Check deadline and late submission policy
        LocalDateTime now = LocalDateTime.now();
        boolean isLate = now.isAfter(assignment.getDueDate());

        if (isLate && !assignment.isAllowLateSubmissions()) {
            throw new BusinessException("The deadline for this assignment has passed and late submissions are not accepted");
        }

        User student = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if student already submitted (allow resubmission / editing)
        Optional<Submission> existing = submissionRepository.findByAssignmentIdAndStudentId(assignmentId, student.getId());
        Submission submission;

        if (existing.isPresent()) {
            submission = existing.get();
            // If already graded, prevent silent overwrite unless permitted
            if (submission.getStatus() == SubmissionStatus.GRADED) {
                throw new BusinessException("This assignment has already been graded and cannot be resubmitted");
            }
            submission.setTextResponse(request.getTextResponse());
            submission.setFilePath(request.getFilePath());
            submission.setFileName(request.getFileName());
            submission.setSubmittedAt(now);
            submission.setStatus(isLate ? SubmissionStatus.LATE : SubmissionStatus.SUBMITTED);
        } else {
            submission = new Submission();
            submission.setAssignment(assignment);
            submission.setStudent(student);
            submission.setTextResponse(request.getTextResponse());
            submission.setFilePath(request.getFilePath());
            submission.setFileName(request.getFileName());
            submission.setSubmittedAt(now);
            submission.setStatus(isLate ? SubmissionStatus.LATE : SubmissionStatus.SUBMITTED);
        }

        Submission saved = submissionRepository.save(submission);
        return SubmissionDto.fromEntity(saved);
    }

    public SubmissionDto getMySubmission(Long assignmentId, UserPrincipal principal) {
        Submission submission = submissionRepository.findByAssignmentIdAndStudentId(assignmentId, principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No submission found for assignment id: " + assignmentId));

        return SubmissionDto.fromEntity(submission);
    }

    public List<SubmissionDto> getSubmissionsByAssignment(Long assignmentId, UserPrincipal principal) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + assignmentId));

        courseService.validateOwnershipOrAdmin(assignment.getCourse(), principal);

        return submissionRepository.findByAssignmentId(assignmentId).stream()
                .map(SubmissionDto::fromEntity)
                .collect(Collectors.toList());
    }

    public SubmissionDto getSubmissionById(Long submissionId, UserPrincipal principal) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with id: " + submissionId));

        // Security check: Only the student owner, course instructor, or Admin can view
        boolean isOwner = submission.getStudent().getId().equals(principal.getId());
        boolean isInstructor = submission.getAssignment().getCourse().getInstructor().getId().equals(principal.getId());
        boolean isAdmin = principal.getRole() == Role.ADMIN;

        if (!isOwner && !isInstructor && !isAdmin) {
            throw new UnauthorizedException("You are not authorized to view this submission");
        }

        return SubmissionDto.fromEntity(submission);
    }
}
