//C:\Users\Shevon\Downloads\authdemo\src\main\java\com\example\authdemo\controller\GroupController.java
package com.example.authdemo.controller;

import com.example.authdemo.model.Group;
import com.example.authdemo.service.GroupService;
import com.example.authdemo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final JwtUtil jwtUtil;

    // Create Group (JWT required)
    @PostMapping("/create")
    public ResponseEntity<Group> createGroup(
            @RequestParam String name,
            @RequestParam String description,
            @RequestHeader("Authorization") String authHeader
    ) {
        String adminEmail = jwtUtil.extractEmail(authHeader.substring(7));
        Group group = Group.builder()
                .name(name)
                .description(description)
                .adminEmail(adminEmail)
                .build();
        return ResponseEntity.ok(groupService.save(group));
    }

    // Update Group (Only admin)
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateGroup(
            @PathVariable String id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestHeader("Authorization") String authHeader
    ) {
        String userEmail = jwtUtil.extractEmail(authHeader.substring(7));
        Group group = groupService.findById(id).orElseThrow();
        if (!group.getAdminEmail().equals(userEmail)) {
            return ResponseEntity.status(403).body("Only admin can edit");
        }
        group.setName(name);
        group.setDescription(description);
        return ResponseEntity.ok(groupService.save(group));
    }

    // Delete Group (Only admin)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteGroup(
            @PathVariable String id,
            @RequestHeader("Authorization") String authHeader
    ) {
        String userEmail = jwtUtil.extractEmail(authHeader.substring(7));
        Group group = groupService.findById(id).orElseThrow();
        if (!group.getAdminEmail().equals(userEmail)) {
            return ResponseEntity.status(403).body("Only admin can delete");
        }
        groupService.deleteById(id);
        return ResponseEntity.ok("Group deleted");
    }

    // Add Member to Group (Admin or member)
    @PutMapping("/{groupId}/add-member")
    public ResponseEntity<Group> addMember(
            @PathVariable String groupId,
            @RequestParam String userEmail,
            @RequestHeader("Authorization") String authHeader
    ) {
        jwtUtil.extractEmail(authHeader.substring(7)); // Validate token
        return ResponseEntity.ok(groupService.addMember(groupId, userEmail));
    }

    // Get All Groups (Public)
    @GetMapping("/all")
    public List<Group> getAllGroups() {
        return groupService.findAll();
    }
}
