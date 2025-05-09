//C:\Users\Shevon\Downloads\authdemo\src\main\java\com\example\authdemo\controller\GroupMessageController.java
package com.example.authdemo.controller;

import com.example.authdemo.model.GroupMessage;
import com.example.authdemo.service.GroupMessageService;
import com.example.authdemo.service.GroupService;
import com.example.authdemo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import com.example.authdemo.model.Group;



@RestController
@RequestMapping("/api/groups/messages")
@RequiredArgsConstructor
public class GroupMessageController {
    private final GroupMessageService messageService;
    private final GroupService groupService;
    private final JwtUtil jwtUtil;

    // Send Message (Group members only)
    @PostMapping("/send/{groupId}")
    public ResponseEntity<?> sendMessage(
            @PathVariable String groupId,
            @RequestParam String content,
            @RequestParam(required = false) MultipartFile file,
            @RequestHeader("Authorization") String authHeader
    ) throws IOException {
        String senderEmail = jwtUtil.extractEmail(authHeader.substring(7));
        Group group = groupService.findById(groupId).orElseThrow();
        
        // Check if user is group member
        if (!group.getMemberEmails().contains(senderEmail)) {
            return ResponseEntity.status(403).body("Not a group member");
        }

        GroupMessage.GroupMessageBuilder builder = GroupMessage.builder()
                .groupId(groupId)
                .senderEmail(senderEmail)
                .content(content)
                .timestamp(new Date());

        if (file != null && !file.isEmpty()) {
            String uploadDir = "C:/Users/Shevon/Downloads/authdemo/uploads";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            String filePath = uploadDir + "/" + file.getOriginalFilename();
            file.transferTo(new File(filePath));
            builder.filePath(filePath);
            builder.fileType(file.getContentType());
        }

        return ResponseEntity.ok(messageService.save(builder.build()));
    }

    // Get Messages in Group (Members only)
    @GetMapping("/group/{groupId}")
    public List<GroupMessage> getGroupMessages(@PathVariable String groupId) {
        return messageService.findByGroupId(groupId);
    }

        // EDIT Message (Sender only)
        @PutMapping("/edit/{messageId}")
        public ResponseEntity<?> editMessage(
                @PathVariable String messageId,
                @RequestParam String content,
                @RequestParam(required = false) MultipartFile file,
                @RequestHeader("Authorization") String authHeader
        ) throws IOException {
            String userEmail = jwtUtil.extractEmail(authHeader.substring(7));
            GroupMessage message = messageService.findById(messageId).orElseThrow();
    
            if (!message.getSenderEmail().equals(userEmail)) {
                return ResponseEntity.status(403).body("Only sender can edit");
            }
    
            message.setContent(content);
            message.setTimestamp(new Date()); // Optionally update timestamp
    
            if (file != null && !file.isEmpty()) {
                String uploadDir = "C:/Users/Shevon/Downloads/authdemo/uploads";
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();
                String filePath = uploadDir + "/" + file.getOriginalFilename();
                file.transferTo(new File(filePath));
                message.setFilePath(filePath);
                message.setFileType(file.getContentType());
            }
    
            return ResponseEntity.ok(messageService.updateMessage(message));
        }
    // Delete Message (Sender only)
    @DeleteMapping("/delete/{messageId}")
    public ResponseEntity<?> deleteMessage(
            @PathVariable String messageId,
            @RequestHeader("Authorization") String authHeader
    ) {
        String userEmail = jwtUtil.extractEmail(authHeader.substring(7));
        GroupMessage message = messageService.findById(messageId).orElseThrow();
        if (!message.getSenderEmail().equals(userEmail)) {
            return ResponseEntity.status(403).body("Only sender can delete");
        }
        messageService.deleteById(messageId);
        return ResponseEntity.ok("Message deleted");
    }
}
