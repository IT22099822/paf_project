package com.example.authdemo.repository;

import com.example.authdemo.model.PostFeedback;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

// Repository interface for managing PostFeedback entities in MongoDB
public interface PostFeedbackRepository extends MongoRepository<PostFeedback, String> {

    // Custom query method to retrieve feedback by associated post ID
    List<PostFeedback> findByPostId(String postId);
}
