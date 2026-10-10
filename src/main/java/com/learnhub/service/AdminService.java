package com.learnhub.service;

import com.learnhub.dto.DashboardStatsDto;
import com.learnhub.entity.Role;
import com.learnhub.entity.SubmissionStatus;
import com.learnhub.repository.CourseRepository;
import com.learnhub.repository.EnrollmentRepository;
import com.learnhub.repository.LessonProgressRepository;
import com.learnhub.repository.SubmissionRepository;
import com.learnhub.repository.UserRepository;
import com.learnhub.security.UserPrincipal;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SubmissionRepository submissionRepository;
    private final LessonProgressRepository lessonProgressRepository;

    public AdminService(
            UserRepository userRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository,
            SubmissionRepository submissionRepository,
            LessonProgressRepository lessonProgressRepository
    ) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.submissionRepository = submissionRepository;
        this.lessonProgressRepository = lessonProgressRepository;
    }

    public DashboardStatsDto getAdminDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();
        stats.setTotalUsers(userRepository.count());
        stats.setTotalStudents(userRepository.countByRole(Role.STUDENT));
        stats.setTotalInstructors(userRepository.countByRole(Role.INSTRUCTOR));
        stats.setTotalCourses(courseRepository.count());
        stats.setPublishedCourses(courseRepository.countByPublished(true));
        stats.setUnpublishedCourses(courseRepository.countByPublished(false));
        stats.setTotalEnrollments(enrollmentRepository.count());
        stats.setTotalSubmissions(submissionRepository.count());
        return stats;
    }

    public DashboardStatsDto getInstructorDashboardStats(UserPrincipal principal) {
        DashboardStatsDto stats = new DashboardStatsDto();
        stats.setInstructorCourses(courseRepository.countByInstructorId(principal.getId()));
        stats.setPendingGradingCount(submissionRepository.countByAssignmentCourseInstructorIdAndStatus(principal.getId(), SubmissionStatus.SUBMITTED));
        stats.setTotalSubmissions(submissionRepository.countByInstructorId(principal.getId()));
        return stats;
    }

    public DashboardStatsDto getStudentDashboardStats(UserPrincipal principal) {
        DashboardStatsDto stats = new DashboardStatsDto();
        stats.setStudentEnrolledCourses(enrollmentRepository.countByStudentId(principal.getId()));
        var submissions = submissionRepository.findByStudentId(principal.getId());
        long graded = submissions.stream().filter(s -> s.getStatus() == SubmissionStatus.GRADED).count();
        long pending = submissions.size() - graded;
        stats.setStudentGradedAssignments(graded);
        stats.setStudentPendingAssignments(pending);
        return stats;
    }
}
