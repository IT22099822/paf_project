// src/main/java/com/example/authdemo/model/SkillSharingPost.java
package com.example.authdemo.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

//.
@Document(collection = "skill_posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillSharingPost {
    @Id
    private String id;
    private String title;
    private String description;
    private String filePath;
    private String fileType;
    private String category;
    private String tags;
    
    @CreatedDate
    private Date createdAt;
    
    private String userEmail; // Connect to User via email
}