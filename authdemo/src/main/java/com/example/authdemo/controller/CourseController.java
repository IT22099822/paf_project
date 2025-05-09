//C:\Users\Shevon\Downloads\authdemo\src\main\java\com\example\authdemo\controller\CourseController.java
package com.example.authdemo.controller;

import com.example.authdemo.model.Course;
import com.example.authdemo.service.CourseService;
import com.example.authdemo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    private final JwtUtil jwtUtil;
    private static final String UPLOAD_DIR = "C:/Users/Shevon/Downloads/authdemo/uploads";

    // CREATE (JWT required)
    @PostMapping("/create")
    public ResponseEntity<?> createCourse(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String category,
            @RequestParam double price,
            @RequestParam String prerequisites,
            @RequestParam String duration,
            @RequestParam List<String> lessonTitles,
            @RequestParam List<String> lessonDescriptions,
            @RequestParam(required = false) List<MultipartFile> mediaFiles,
            @RequestParam(required = false) MultipartFile coverImage,
            @RequestHeader("Authorization") String authHeader
    ) throws IOException {
        String instructorEmail = jwtUtil.extractEmail(authHeader.substring(7));

        // Build lessons
        List<Course.Lesson> lessons = new ArrayList<>();
        for (int i = 0; i < lessonTitles.size(); i++) {
            Course.Lesson lesson = Course.Lesson.builder()
                    .title(lessonTitles.get(i))
                    .description(lessonDescriptions.get(i))
                    .media(new ArrayList<>())
                    .build();
            lessons.add(lesson);
        }

        // Handle media files
        if (mediaFiles != null && !mediaFiles.isEmpty()) {
            int mediaIndex = 0;
            for (MultipartFile file : mediaFiles) {
                String filePath = saveFile(file);
                Course.Media media = Course.Media.builder()
                        .fileName(file.getOriginalFilename())
                        .filePath(filePath)
                        .fileType(file.getContentType())
                        .build();
                lessons.get(mediaIndex % lessons.size()).getMedia().add(media);
                mediaIndex++;
            }
        }

        // Handle cover image
        String coverImagePath = null;
        String coverImageType = null;
        if (coverImage != null && !coverImage.isEmpty()) {
            coverImagePath = saveFile(coverImage);
            coverImageType = coverImage.getContentType();
        }

        Course course = Course.builder()
                .title(title)
                .description(description)
                .category(category)
                .price(price)
                .prerequisites(prerequisites)
                .duration(duration)
                .instructorEmail(instructorEmail)
                .lessons(lessons)
                .coverImagePath(coverImagePath)
                .coverImageType(coverImageType)
                .build();

        return ResponseEntity.ok(courseService.save(course));
    }

    // READ (public)
    @GetMapping("/all")
    public List<Course> getAllCourses() {
        return courseService.findAll();
    }

    // UPDATE (JWT required, instructor only)
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCourse(
            @PathVariable String id,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String category,
            @RequestParam double price,
            @RequestParam String prerequisites,
            @RequestParam String duration,
            @RequestHeader("Authorization") String authHeader
    ) {
        String instructorEmail = jwtUtil.extractEmail(authHeader.substring(7));
        Course course = courseService.findById(id).orElseThrow();
        if (!course.getInstructorEmail().equals(instructorEmail))
            return ResponseEntity.status(403).body("Only instructor can update");

        course.setTitle(title);
        course.setDescription(description);
        course.setCategory(category);
        course.setPrice(price);
        course.setPrerequisites(prerequisites);
        course.setDuration(duration);
        return ResponseEntity.ok(courseService.save(course));
    }

    // DELETE (JWT required, instructor only)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCourse(
            @PathVariable String id,
            @RequestHeader("Authorization") String authHeader
    ) {
        String instructorEmail = jwtUtil.extractEmail(authHeader.substring(7));
        Course course = courseService.findById(id).orElseThrow();
        if (!course.getInstructorEmail().equals(instructorEmail))
            return ResponseEntity.status(403).body("Only instructor can delete");
        courseService.deleteById(id);
        return ResponseEntity.ok("Course deleted");
    }

    private String saveFile(MultipartFile file) throws IOException {
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) dir.mkdirs();
        String filePath = UPLOAD_DIR + "/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        file.transferTo(new File(filePath));
        return filePath;
    }
}
