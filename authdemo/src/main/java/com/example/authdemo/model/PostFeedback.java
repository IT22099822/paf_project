package com.example.authdemo.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "post_feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostFeedback {
    @Id
    private String id;
    private String postId; // The SkillSharingPost this feedback belongs to
    private String userEmail; // Who left the feedback
    private String comment;
    private Integer rating; // 1-5
    private String picturePath; // Optional
    private String pictureType; // Optional
}
