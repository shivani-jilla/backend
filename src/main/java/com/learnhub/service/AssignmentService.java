package com.learnhub.service;

import com.learnhub.dto.AssignmentCreateRequest;
import com.learnhub.dto.AssignmentDto;
import com.learnhub.dto.SubmissionDto;
import com.learnhub.entity.Assignment;
import com.learnhub.entity.Course;
import com.learnhub.entity.Role;
import com.learnhub.entity.Submission;
import com.learnhub.exception.BusinessException;
import com.learnhub.exception.ResourceNotFoundException;
import com.learnhub.repository.AssignmentRepository;
import com.learnhub.repository.CourseRepository;
import com.learnhub.repository.EnrollmentRepository;
import com.learnhub.repository.SubmissionRepository;
import com.learnhub.security.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SubmissionRepository submissionRepository;
    private final CourseService courseService;

    public AssignmentService(
            AssignmentRepository assignmentRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository,
            SubmissionRepository submissionRepository,
            CourseService courseService
    ) {
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.submissionRepository = submissionRepository;
        this.courseService = courseService;
    }

    public List<AssignmentDto> getAssignmentsByCourse(Long courseId, UserPrincipal principal) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        if (principal.getRole() == Role.STUDENT) {
            if (!enrollmentRepository.existsByStudentIdAndCourseId(principal.getId(), courseId)) {
                throw new BusinessException("You must be enrolled in the course to view assignments");
            }
        }

        List<Assignment> assignments;
        if (principal.getRole() == Role.STUDENT) {
            assignments = assignmentRepository.findByCourseIdAndPublishedTrueOrderByDueDateAsc(courseId);
        } else {
            assignments = assignmentRepository.findByCourseId(courseId);
        }

        return assignments.stream()
                .map(assignment -> {
                    AssignmentDto dto = AssignmentDto.fromEntity(assignment);
                    if (principal.getRole() == Role.STUDENT) {
                        Optional<Submission> sub = submissionRepository
                                .findByAssignmentIdAndStudentId(assignment.getId(), principal.getId());
                        if (sub.isPresent()) {
                            dto.setHasSubmitted(true);
                            dto.setMySubmission(SubmissionDto.fromEntity(sub.get()));
                        } else {
                            dto.setHasSubmitted(false);
                        }
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public AssignmentDto getAssignmentById(Long assignmentId, UserPrincipal principal) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + assignmentId));

        Long courseId = assignment.getCourse().getId();

        if (principal.getRole() == Role.STUDENT) {
            if (!enrollmentRepository.existsByStudentIdAndCourseId(principal.getId(), courseId)) {
                throw new BusinessException("You must be enrolled in the course to view this assignment");
            }
        }

        AssignmentDto dto = AssignmentDto.fromEntity(assignment);
        if (principal.getRole() == Role.STUDENT) {
            Optional<Submission> sub = submissionRepository
                    .findByAssignmentIdAndStudentId(assignment.getId(), principal.getId());
            if (sub.isPresent()) {
                dto.setHasSubmitted(true);
                dto.setMySubmission(SubmissionDto.fromEntity(sub.get()));
            } else {
                dto.setHasSubmitted(false);
            }
        }
        return dto;
    }

    @Transactional
    public AssignmentDto createAssignment(Long courseId, AssignmentCreateRequest request, UserPrincipal principal) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        courseService.validateOwnershipOrAdmin(course, principal);

        Assignment assignment = new Assignment();
        assignment.setCourse(course);
        assignment.setTitle(request.getTitle().trim());
        assignment.setDescription(request.getDescription());
        assignment.setInstructions(request.getInstructions());
        assignment.setMaxMarks(request.getMaxMarks());
        assignment.setDueDate(request.getDueDate());
        assignment.setAllowedSubmissionType(request.getAllowedSubmissionType());
        assignment.setAttachmentPath(request.getAttachmentPath());
        assignment.setAllowLateSubmissions(request.isAllowLateSubmissions());
        assignment.setPublished(request.isPublished());

        Assignment saved = assignmentRepository.save(assignment);
        return AssignmentDto.fromEntity(saved);
    }

    @Transactional
    public AssignmentDto updateAssignment(Long assignmentId, AssignmentCreateRequest request, UserPrincipal principal) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + assignmentId));

        courseService.validateOwnershipOrAdmin(assignment.getCourse(), principal);

        assignment.setTitle(request.getTitle().trim());
        assignment.setDescription(request.getDescription());
        assignment.setInstructions(request.getInstructions());
        assignment.setMaxMarks(request.getMaxMarks());
        assignment.setDueDate(request.getDueDate());
        assignment.setAllowedSubmissionType(request.getAllowedSubmissionType());
        assignment.setAttachmentPath(request.getAttachmentPath());
        assignment.setAllowLateSubmissions(request.isAllowLateSubmissions());
        assignment.setPublished(request.isPublished());

        Assignment updated = assignmentRepository.save(assignment);
        return AssignmentDto.fromEntity(updated);
    }

    @Transactional
    public void deleteAssignment(Long assignmentId, UserPrincipal principal) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + assignmentId));

        courseService.validateOwnershipOrAdmin(assignment.getCourse(), principal);

        assignmentRepository.delete(assignment);
    }
}
