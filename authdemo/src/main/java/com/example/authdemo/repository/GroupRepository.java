package com.example.authdemo.repository;

import com.example.authdemo.model.Group;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface GroupRepository extends MongoRepository<Group, String> {
    List<Group> findByAdminEmail(String adminEmail);
}
