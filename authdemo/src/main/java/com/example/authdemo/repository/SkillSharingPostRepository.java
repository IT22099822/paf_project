package com.example.authdemo.repository;

import com.example.authdemo.model.SkillSharingPost;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface SkillSharingPostRepository extends MongoRepository<SkillSharingPost, String> {
    List<SkillSharingPost> findByUserEmail(String userEmail);
}
