package com.learnhub.config;

import com.learnhub.entity.*;
import com.learnhub.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository,
            CourseRepository courseRepository,
            ModuleRepository moduleRepository,
            LessonRepository lessonRepository,
            EnrollmentRepository enrollmentRepository,
            LessonProgressRepository lessonProgressRepository,
            AssignmentRepository assignmentRepository,
            SubmissionRepository submissionRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
        this.lessonRepository = lessonRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.lessonProgressRepository = lessonProgressRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            return; // Already initialized
        }

        // 1. Create Default Users
        User admin = new User(null, "Platform Admin", "admin@learnhub.com",
                passwordEncoder.encode("Admin@123"), Role.ADMIN, true);
        userRepository.save(admin);

        User instructor = new User(null, "Dr. Sarah Jenkins", "instructor@learnhub.com",
                passwordEncoder.encode("Instructor@123"), Role.INSTRUCTOR, true);
        userRepository.save(instructor);

        User instructor2 = new User(null, "Alex Rivera", "alex@learnhub.com",
                passwordEncoder.encode("Instructor@123"), Role.INSTRUCTOR, true);
        userRepository.save(instructor2);

        User student = new User(null, "Sridhar Sharma", "student@learnhub.com",
                passwordEncoder.encode("Student@123"), Role.STUDENT, true);
        userRepository.save(student);

        // 2. Course 1: Full-Stack React & Spring Boot
        Course course1 = new Course();
        course1.setTitle("Full-Stack Web Development with React & Spring Boot");
        course1.setShortDescription("Master modern full-stack development building end-to-end applications from scratch.");
        course1.setDescription("An in-depth, hands-on masterclass combining Spring Boot 3, Spring Security, JWT, JPA/Hibernate, MySQL, and modern React 18 with Vite. Learn production-grade software patterns, REST architecture, and clean UI engineering.");
        course1.setCategory("Web Development");
        course1.setDifficulty("Intermediate");
        course1.setInstructor(instructor);
        course1.setDuration("12 Weeks");
        course1.setThumbnailPath("https://images.unsplash.com/photo-1498050108023-c5249f4df085?auto=format&fit=crop&w=800&q=80");
        course1.setLearningObjectives("• Design and implement RESTful APIs with Spring Boot 3\n• Secure backend endpoints using Spring Security & JWT\n• Build responsive, accessible frontends using React and Vite\n• Model relational databases using MySQL and Hibernate JPA");
        course1.setPrerequisites("Basic understanding of Java and JavaScript fundamentals.");
        course1.setPublished(true);
        course1 = courseRepository.save(course1);

        // Modules & Lessons for Course 1
        CourseModule mod1 = new CourseModule();
        mod1.setTitle("Module 1: Backend Architecture & REST APIs");
        mod1.setDescription("Set up Spring Boot, entity models, and robust REST controllers.");
        mod1.setDisplayOrder(1);
        mod1.setCourse(course1);
        mod1 = moduleRepository.save(mod1);

        Lesson l1 = new Lesson();
        l1.setTitle("1.1 Introduction to Spring Boot 3 & Project Structure");
        l1.setDescription("Explore Spring Boot core components, dependency injection, and layered architecture.");
        l1.setVideoUrl("https://www.w3schools.com/html/mov_bbb.mp4");
        l1.setDuration("15m 30s");
        l1.setDisplayOrder(1);
        l1.setPublished(true);
        l1.setModule(mod1);
        lessonRepository.save(l1);

        Lesson l2 = new Lesson();
        l2.setTitle("1.2 Spring Data JPA & MySQL Configuration");
        l2.setDescription("Connect Spring Boot to MySQL and build entities with JPA annotations.");
        l2.setVideoUrl("https://www.w3schools.com/html/mov_bbb.mp4");
        l2.setDuration("22m 10s");
        l2.setDisplayOrder(2);
        l2.setPublished(true);
        l2.setModule(mod1);
        lessonRepository.save(l2);

        CourseModule mod2 = new CourseModule();
        mod2.setTitle("Module 2: Frontend Engineering with React & Vite");
        mod2.setDescription("Create scalable frontend components with React, Vite, and Axios.");
        mod2.setDisplayOrder(2);
        mod2.setCourse(course1);
        mod2 = moduleRepository.save(mod2);

        Lesson l3 = new Lesson();
        l3.setTitle("2.1 Building Modern SPAs with Vite & React");
        l3.setDescription("Understand component trees, routing with React Router, and state handling.");
        l3.setVideoUrl("https://www.w3schools.com/html/mov_bbb.mp4");
        l3.setDuration("18m 45s");
        l3.setDisplayOrder(1);
        l3.setPublished(true);
        l3.setModule(mod2);
        lessonRepository.save(l3);

        // Assignment for Course 1
        Assignment a1 = new Assignment();
        a1.setCourse(course1);
        a1.setTitle("Project 1: Secure JWT Authentication Service");
        a1.setDescription("Implement a production-grade registration and login endpoint with BCrypt password hashing and JWT issuance.");
        a1.setInstructions("1. Implement User entity and UserRepository\n2. Add JwtService for token signing\n3. Protect sensitive endpoints with OncePerRequestFilter\n4. Submit your GitHub link or ZIP archive with test results.");
        a1.setMaxMarks(new BigDecimal("100.00"));
        a1.setDueDate(LocalDateTime.now().plusDays(7));
        a1.setAllowedSubmissionType(SubmissionType.BOTH);
        a1.setAllowLateSubmissions(true);
        a1.setPublished(true);
        a1 = assignmentRepository.save(a1);

        // 3. Course 2: Data Structures & Algorithms in Java
        Course course2 = new Course();
        course2.setTitle("Mastering Data Structures & Algorithms in Java");
        course2.setShortDescription("Deep dive into essential algorithms, tree structures, graph theory, and dynamic programming.");
        course2.setDescription("Prepare for technical interviews and competitive programming. Master asymptotic complexity analysis, divide and conquer, greedy algorithms, and advanced graph traversals.");
        course2.setCategory("Computer Science");
        course2.setDifficulty("Advanced");
        course2.setInstructor(instructor);
        course2.setDuration("8 Weeks");
        course2.setThumbnailPath("https://images.unsplash.com/photo-1516116211227-bbc13c744be6?auto=format&fit=crop&w=800&q=80");
        course2.setLearningObjectives("• Master Big-O analysis and space complexity\n• Implement self-balancing binary search trees (AVL, Red-Black)\n• Solve dynamic programming problems using memoization and tabulation");
        course2.setPrerequisites("Solid grasp of Java syntax and Object-Oriented principles.");
        course2.setPublished(true);
        course2 = courseRepository.save(course2);

        CourseModule dsaMod = new CourseModule();
        dsaMod.setTitle("Module 1: Trees, Graphs & Traversal");
        dsaMod.setDescription("Binary trees, heaps, depth-first search, and breadth-first search.");
        dsaMod.setDisplayOrder(1);
        dsaMod.setCourse(course2);
        dsaMod = moduleRepository.save(dsaMod);

        Lesson dsaL1 = new Lesson();
        dsaL1.setTitle("1.1 Binary Search Tree Properties & Operations");
        dsaL1.setDescription("Detailed analysis of insertions, deletions, and balance properties.");
        dsaL1.setVideoUrl("https://www.w3schools.com/html/mov_bbb.mp4");
        dsaL1.setDuration("28m 00s");
        dsaL1.setDisplayOrder(1);
        dsaL1.setPublished(true);
        dsaL1.setModule(dsaMod);
        lessonRepository.save(dsaL1);

        Assignment a2 = new Assignment();
        a2.setCourse(course2);
        a2.setTitle("Assignment: Self-Balancing AVL Tree Implementation");
        a2.setDescription("Write Java code for an AVL tree with left/right rotation and height balance verification.");
        a2.setInstructions("Submit your Java source code file along with unit test verification showing log(n) lookup guarantees.");
        a2.setMaxMarks(new BigDecimal("50.00"));
        a2.setDueDate(LocalDateTime.now().plusDays(10));
        a2.setAllowedSubmissionType(SubmissionType.BOTH);
        a2.setAllowLateSubmissions(false);
        a2.setPublished(true);
        assignmentRepository.save(a2);

        // 4. Course 3: Python for Data Science
        Course course3 = new Course();
        course3.setTitle("Python for Data Science & Machine Learning Bootcamp");
        course3.setShortDescription("Learn NumPy, Pandas, Matplotlib, Scikit-Learn, and applied data exploration.");
        course3.setDescription("A complete beginner-to-advanced pathway to modern data analytics and machine learning with Python. Covers regression, classification, clustering, and data visualization pipelines.");
        course3.setCategory("Data Science");
        course3.setDifficulty("Beginner");
        course3.setInstructor(instructor2);
        course3.setDuration("10 Weeks");
        course3.setThumbnailPath("https://images.unsplash.com/photo-1551288049-bebda4e38f71?auto=format&fit=crop&w=800&q=80");
        course3.setLearningObjectives("• Clean, filter, and transform real-world datasets with Pandas\n• Create publication-quality charts with Seaborn\n• Train and evaluate supervised machine learning models");
        course3.setPrerequisites("No prior programming experience required.");
        course3.setPublished(true);
        course3 = courseRepository.save(course3);

        CourseModule dsMod = new CourseModule();
        dsMod.setTitle("Module 1: Foundations of NumPy & Pandas");
        dsMod.setDescription("Array manipulation and dataframe slicing essentials.");
        dsMod.setDisplayOrder(1);
        dsMod.setCourse(course3);
        dsMod = moduleRepository.save(dsMod);

        Lesson dsL1 = new Lesson();
        dsL1.setTitle("1.1 Data Structures in Pandas: Series & DataFrames");
        dsL1.setDescription("Introduction to indexing, column selection, and mathematical transformations.");
        dsL1.setVideoUrl("https://www.w3schools.com/html/mov_bbb.mp4");
        dsL1.setDuration("19m 12s");
        dsL1.setDisplayOrder(1);
        dsL1.setPublished(true);
        dsL1.setModule(dsMod);
        lessonRepository.save(dsL1);

        // 5. Enroll Student in Course 1 and Course 3
        Enrollment e1 = new Enrollment(student, course1);
        enrollmentRepository.save(e1);

        Enrollment e3 = new Enrollment(student, course3);
        enrollmentRepository.save(e3);

        // 6. Complete Lesson 1 for Student
        LessonProgress lp1 = new LessonProgress(student, l1);
        lp1.setCompleted(true);
        lp1.setCompletedAt(LocalDateTime.now().minusDays(1));
        lessonProgressRepository.save(lp1);

        // 7. Seed Student Submission for Assignment 1 (Graded)
        Submission sub1 = new Submission();
        sub1.setAssignment(a1);
        sub1.setStudent(student);
        sub1.setTextResponse("Completed full implementation with Spring Security 6 filter chain, JWT verification, and test coverage.\nRepository link: https://github.com/example/auth-service");
        sub1.setSubmittedAt(LocalDateTime.now().minusDays(2));
        sub1.setStatus(SubmissionStatus.GRADED);
        sub1.setMarksAwarded(new BigDecimal("95.00"));
        sub1.setFeedback("Excellent work! Clean filter implementation and thorough test cases. Meets all enterprise requirements.");
        sub1.setGradedAt(LocalDateTime.now().minusDays(1));
        sub1.setGradedBy(instructor);
        submissionRepository.save(sub1);
    }
}
