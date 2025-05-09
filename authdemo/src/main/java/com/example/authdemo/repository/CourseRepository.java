package com.example.authdemo.repository;

import com.example.authdemo.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CourseRepository extends MongoRepository<Course, String> {
    List<Course> findByInstructorEmail(String instructorEmail);
}
