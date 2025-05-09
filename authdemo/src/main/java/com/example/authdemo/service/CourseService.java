package com.example.authdemo.service;

import com.example.authdemo.model.Course;
import com.example.authdemo.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepo;

    public Course save(Course course) { return courseRepo.save(course); }
    public Optional<Course> findById(String id) { return courseRepo.findById(id); }
    public List<Course> findAll() { return courseRepo.findAll(); }
    public List<Course> findByInstructorEmail(String email) { return courseRepo.findByInstructorEmail(email); }
    public void deleteById(String id) { courseRepo.deleteById(id); }
}
