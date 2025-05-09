package com.example.authdemo.repository;

import com.example.authdemo.model.PostFeedback;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PostFeedbackRepository extends MongoRepository<PostFeedback, String> {
    List<PostFeedback> findByPostId(String postId);
}
