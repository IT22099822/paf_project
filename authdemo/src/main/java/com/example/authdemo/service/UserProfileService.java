//src/main/java/com/example/authdemo/service/UserProfileService.java
package com.example.authdemo.service;

import com.example.authdemo.model.UserProfile;
import com.example.authdemo.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository repo;

    public UserProfile save(UserProfile profile) { return repo.save(profile); }
    public Optional<UserProfile> findById(String id) { return repo.findById(id); }
    public Optional<UserProfile> findByUserEmail(String userEmail) { return repo.findByUserEmail(userEmail); }
    public List<UserProfile> findAll() { return repo.findAll(); }
    public void deleteById(String id) { repo.deleteById(id); }
}
