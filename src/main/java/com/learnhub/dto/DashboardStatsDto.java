package com.learnhub.dto;

import java.util.List;

public class DashboardStatsDto {

    // Admin Stats
    private Long totalUsers;
    private Long totalStudents;
    private Long totalInstructors;
    private Long totalCourses;
    private Long publishedCourses;
    private Long unpublishedCourses;
    private Long totalEnrollments;
    private Long totalSubmissions;

    // Instructor Stats
    private Long instructorCourses;
    private Long instructorStudents;
    private Long pendingGradingCount;
    private Long instructorAssignments;

    // Student Stats
    private Long studentEnrolledCourses;
    private Long studentCompletedCourses;
    private Long studentCompletedLessons;
    private Long studentPendingAssignments;
    private Long studentGradedAssignments;
    private Double averageProgress;

    // Recent items
    private List<CourseDto> recentCourses;
    private List<SubmissionDto> recentSubmissions;
    private List<AssignmentDto> upcomingAssignments;

    public DashboardStatsDto() {
    }

    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(Long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public Long getTotalInstructors() {
        return totalInstructors;
    }

    public void setTotalInstructors(Long totalInstructors) {
        this.totalInstructors = totalInstructors;
    }

    public Long getTotalCourses() {
        return totalCourses;
    }

    public void setTotalCourses(Long totalCourses) {
        this.totalCourses = totalCourses;
    }

    public Long getPublishedCourses() {
        return publishedCourses;
    }

    public void setPublishedCourses(Long publishedCourses) {
        this.publishedCourses = publishedCourses;
    }

    public Long getUnpublishedCourses() {
        return unpublishedCourses;
    }

    public void setUnpublishedCourses(Long unpublishedCourses) {
        this.unpublishedCourses = unpublishedCourses;
    }

    public Long getTotalEnrollments() {
        return totalEnrollments;
    }

    public void setTotalEnrollments(Long totalEnrollments) {
        this.totalEnrollments = totalEnrollments;
    }

    public Long getTotalSubmissions() {
        return totalSubmissions;
    }

    public void setTotalSubmissions(Long totalSubmissions) {
        this.totalSubmissions = totalSubmissions;
    }

    public Long getInstructorCourses() {
        return instructorCourses;
    }

    public void setInstructorCourses(Long instructorCourses) {
        this.instructorCourses = instructorCourses;
    }

    public Long getInstructorStudents() {
        return instructorStudents;
    }

    public void setInstructorStudents(Long instructorStudents) {
        this.instructorStudents = instructorStudents;
    }

    public Long getPendingGradingCount() {
        return pendingGradingCount;
    }

    public void setPendingGradingCount(Long pendingGradingCount) {
        this.pendingGradingCount = pendingGradingCount;
    }

    public Long getInstructorAssignments() {
        return instructorAssignments;
    }

    public void setInstructorAssignments(Long instructorAssignments) {
        this.instructorAssignments = instructorAssignments;
    }

    public Long getStudentEnrolledCourses() {
        return studentEnrolledCourses;
    }

    public void setStudentEnrolledCourses(Long studentEnrolledCourses) {
        this.studentEnrolledCourses = studentEnrolledCourses;
    }

    public Long getStudentCompletedCourses() {
        return studentCompletedCourses;
    }

    public void setStudentCompletedCourses(Long studentCompletedCourses) {
        this.studentCompletedCourses = studentCompletedCourses;
    }

    public Long getStudentCompletedLessons() {
        return studentCompletedLessons;
    }

    public void setStudentCompletedLessons(Long studentCompletedLessons) {
        this.studentCompletedLessons = studentCompletedLessons;
    }

    public Long getStudentPendingAssignments() {
        return studentPendingAssignments;
    }

    public void setStudentPendingAssignments(Long studentPendingAssignments) {
        this.studentPendingAssignments = studentPendingAssignments;
    }

    public Long getStudentGradedAssignments() {
        return studentGradedAssignments;
    }

    public void setStudentGradedAssignments(Long studentGradedAssignments) {
        this.studentGradedAssignments = studentGradedAssignments;
    }

    public Double getAverageProgress() {
        return averageProgress;
    }

    public void setAverageProgress(Double averageProgress) {
        this.averageProgress = averageProgress;
    }

    public List<CourseDto> getRecentCourses() {
        return recentCourses;
    }

    public void setRecentCourses(List<CourseDto> recentCourses) {
        this.recentCourses = recentCourses;
    }

    public List<SubmissionDto> getRecentSubmissions() {
        return recentSubmissions;
    }

    public void setRecentSubmissions(List<SubmissionDto> recentSubmissions) {
        this.recentSubmissions = recentSubmissions;
    }

    public List<AssignmentDto> getUpcomingAssignments() {
        return upcomingAssignments;
    }

    public void setUpcomingAssignments(List<AssignmentDto> upcomingAssignments) {
        this.upcomingAssignments = upcomingAssignments;
    }
}
