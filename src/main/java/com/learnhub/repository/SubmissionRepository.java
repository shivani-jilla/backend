package com.learnhub.repository;

import com.learnhub.entity.Submission;
import com.learnhub.entity.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    List<Submission> findByAssignmentId(Long assignmentId);

    Optional<Submission> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);

    List<Submission> findByStudentId(Long studentId);

    @Query("SELECT s FROM Submission s WHERE s.assignment.course.id = :courseId")
    List<Submission> findByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT s FROM Submission s WHERE s.assignment.course.id = :courseId AND s.student.id = :studentId")
    List<Submission> findByCourseIdAndStudentId(@Param("courseId") Long courseId, @Param("studentId") Long studentId);

    long countByAssignmentCourseInstructorIdAndStatus(Long instructorId, SubmissionStatus status);

    long countByStatus(SubmissionStatus status);

    @Query("SELECT COUNT(s) FROM Submission s WHERE s.assignment.course.instructor.id = :instructorId")
    long countByInstructorId(@Param("instructorId") Long instructorId);
}
