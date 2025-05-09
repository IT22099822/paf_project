package com.example.authdemo.service;

import com.example.authdemo.model.Group;
import com.example.authdemo.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepo;

    public Group save(Group group) { return groupRepo.save(group); }
    public Optional<Group> findById(String id) { return groupRepo.findById(id); }
    public List<Group> findAll() { return groupRepo.findAll(); }
    public void deleteById(String id) { groupRepo.deleteById(id); }

    public Group addMember(String groupId, String userEmail) {
        Group group = groupRepo.findById(groupId).orElseThrow();
        if (!group.getMemberEmails().contains(userEmail)) {
            group.getMemberEmails().add(userEmail);
            return groupRepo.save(group);
        }
        return group;
    }
    public List<Group> findByAdminEmail(String adminEmail) {
        return groupRepo.findByAdminEmail(adminEmail);
    }
    
}
