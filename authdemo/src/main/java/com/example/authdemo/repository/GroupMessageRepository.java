package com.example.authdemo.repository;

import com.example.authdemo.model.GroupMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface GroupMessageRepository extends MongoRepository<GroupMessage, String> {
    List<GroupMessage> findByGroupId(String groupId);
}
