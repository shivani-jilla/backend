package com.learnhub.repository;

import com.learnhub.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Page<Course> findByPublishedTrue(Pageable pageable);

    Page<Course> findByInstructorId(Long instructorId, Pageable pageable);

    List<Course> findByInstructorId(Long instructorId);

    long countByPublished(boolean published);

    long countByInstructorId(Long instructorId);

    @Query(value = "SELECT c FROM Course c LEFT JOIN FETCH c.instructor WHERE " +
           "(c.published = true) AND " +
           "(:search IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.shortDescription) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:category IS NULL OR :category = '' OR c.category = :category) AND " +
           "(:difficulty IS NULL OR :difficulty = '' OR c.difficulty = :difficulty)",
           countQuery = "SELECT COUNT(c) FROM Course c WHERE " +
           "(c.published = true) AND " +
           "(:search IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.shortDescription) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:category IS NULL OR :category = '' OR c.category = :category) AND " +
           "(:difficulty IS NULL OR :difficulty = '' OR c.difficulty = :difficulty)")
    Page<Course> searchPublishedCourses(
            @Param("search") String search,
            @Param("category") String category,
            @Param("difficulty") String difficulty,
            Pageable pageable
    );

    @Query(value = "SELECT c FROM Course c LEFT JOIN FETCH c.instructor WHERE " +
           "(:search IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:category IS NULL OR :category = '' OR c.category = :category) AND " +
           "(:difficulty IS NULL OR :difficulty = '' OR c.difficulty = :difficulty) AND " +
           "(:published IS NULL OR c.published = :published)",
           countQuery = "SELECT COUNT(c) FROM Course c WHERE " +
           "(:search IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:category IS NULL OR :category = '' OR c.category = :category) AND " +
           "(:difficulty IS NULL OR :difficulty = '' OR c.difficulty = :difficulty) AND " +
           "(:published IS NULL OR c.published = :published)")
    Page<Course> searchAllCoursesAdmin(
            @Param("search") String search,
            @Param("category") String category,
            @Param("difficulty") String difficulty,
            @Param("published") Boolean published,
            Pageable pageable
    );
}
