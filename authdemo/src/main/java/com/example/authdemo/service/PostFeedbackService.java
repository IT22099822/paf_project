//C:\Users\Shevon\Downloads\authdemo\src\main\java\com\example\authdemo\service\PostFeedbackService.java
package com.example.authdemo.service;

import com.example.authdemo.model.PostFeedback;
import com.example.authdemo.repository.PostFeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostFeedbackService {
    private final PostFeedbackRepository repo;

    public PostFeedback save(PostFeedback feedback) { return repo.save(feedback); }
    public Optional<PostFeedback> findById(String id) { return repo.findById(id); }
    public List<PostFeedback> findByPostId(String postId) { return repo.findByPostId(postId); }
    public void deleteById(String id) { repo.deleteById(id); }
}
