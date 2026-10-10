package com.learnhub.repository;

import com.learnhub.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findByModuleIdOrderByDisplayOrderAsc(Long moduleId);

    long countByModuleCourseIdAndPublishedTrue(Long courseId);

    List<Lesson> findByModuleCourseIdAndPublishedTrueOrderByModuleDisplayOrderAscDisplayOrderAsc(Long courseId);
}
