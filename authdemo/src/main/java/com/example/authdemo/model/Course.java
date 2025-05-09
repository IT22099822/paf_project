package com.example.authdemo.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {
    @Id
    private String id;
    private String title;
    private String description;
    private String category;
    private double price;
    private String prerequisites;
    private String duration;
    private String instructorEmail; // User who created the course
    private List<Lesson> lessons;
    private String coverImagePath;
    private String coverImageType;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Lesson {
        private String title;
        private String description;
        private List<Media> media;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Media {
        private String fileName;
        private String filePath;
        private String fileType; // e.g., "video/mp4", "application/pdf"
    }
}
