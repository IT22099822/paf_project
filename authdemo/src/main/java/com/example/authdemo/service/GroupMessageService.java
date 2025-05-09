//C:\Users\Shevon\Downloads\authdemo\src\main\java\com\example\authdemo\service\GroupMessageService.java
package com.example.authdemo.service;

import com.example.authdemo.model.GroupMessage;
import com.example.authdemo.repository.GroupMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupMessageService {
    private final GroupMessageRepository messageRepo;

    public GroupMessage save(GroupMessage message) { return messageRepo.save(message); }
    public Optional<GroupMessage> findById(String id) { return messageRepo.findById(id); }
    public List<GroupMessage> findByGroupId(String groupId) { return messageRepo.findByGroupId(groupId); }
    public void deleteById(String id) { messageRepo.deleteById(id); }
    public GroupMessage updateMessage(GroupMessage message) {
        return messageRepo.save(message);
    }
}
