package com.example.authdemo.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {
    @Id
    private String id;
    private String userEmail; // Unique, links to auth user
    private String name;
    private String bio;
    private String profilePicturePath;
    private String profilePictureType;
    private String location;
    private List<String> skills;     // List of skills/interests
    private List<String> socialLinks; // List of URLs
}
