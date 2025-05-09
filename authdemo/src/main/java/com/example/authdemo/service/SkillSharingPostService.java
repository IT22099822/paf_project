//C:\Users\Shevon\Downloads\authdemo\src\main\java\com\example\authdemo\service\SkillSharingPostService.java
package com.example.authdemo.service;

import com.example.authdemo.model.SkillSharingPost;
import com.example.authdemo.repository.SkillSharingPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SkillSharingPostService {
    private final SkillSharingPostRepository postRepo;

    public SkillSharingPost save(SkillSharingPost post) {
        return postRepo.save(post);
    }

    public Optional<SkillSharingPost> findById(String id) {
        return postRepo.findById(id);
    }

    public List<SkillSharingPost> findAll() {
        return postRepo.findAll();
    }

    public List<SkillSharingPost> findByUserEmail(String email) {
        return postRepo.findByUserEmail(email);
    }

    public void deleteById(String id) {
        postRepo.deleteById(id);
    }
}
