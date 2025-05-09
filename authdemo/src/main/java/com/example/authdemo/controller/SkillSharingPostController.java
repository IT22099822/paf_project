//C:\Users\Shevon\Downloads\authdemo\src\main\java\com\example\authdemo\controller\SkillSharingPostController.java
package com.example.authdemo.controller;

import com.example.authdemo.model.SkillSharingPost;
import com.example.authdemo.service.SkillSharingPostService;
import com.example.authdemo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/skill-posts")
@RequiredArgsConstructor
public class SkillSharingPostController {
    private final SkillSharingPostService postService;
    private final JwtUtil jwtUtil;

    // CREATE (Protected, needs JWT)
    @PostMapping("/create")
    public ResponseEntity<?> createPost(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String category,
            @RequestParam String tags,
            @RequestParam(required = false) MultipartFile file,
            @RequestHeader("Authorization") String authHeader
    ) throws IOException {
        String userEmail = jwtUtil.extractEmail(authHeader.substring(7));
        SkillSharingPost.SkillSharingPostBuilder builder = SkillSharingPost.builder()
                .title(title)
                .description(description)
                .category(category)
                .tags(tags)
                .userEmail(userEmail);

        if (file != null && !file.isEmpty()) {
            String uploadDir = "C:/Users/Shevon/Downloads/authdemo/uploads";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            String filePath = uploadDir + "/" + file.getOriginalFilename();
            file.transferTo(new File(filePath));
            builder.filePath(filePath);
            builder.fileType(file.getContentType());
        }

        SkillSharingPost post = postService.save(builder.build());
        return ResponseEntity.ok(post);
    }

    // READ 1 (Public)
    @GetMapping("/all")
    public List<SkillSharingPost> getAllPosts() {
        return postService.findAll();
    }

    // READ 2 (Authenticated user's posts)
    @GetMapping("/my")
    public List<SkillSharingPost> getMyPosts(@RequestHeader("Authorization") String authHeader) {
        String userEmail = jwtUtil.extractEmail(authHeader.substring(7));
        return postService.findByUserEmail(userEmail);
    }

    // UPDATE (Authenticated user, only their own post)
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePost(
            @PathVariable String id,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String category,
            @RequestParam String tags,
            @RequestParam(required = false) MultipartFile file,
            @RequestHeader("Authorization") String authHeader
    ) throws IOException {
        String userEmail = jwtUtil.extractEmail(authHeader.substring(7));
        Optional<SkillSharingPost> optPost = postService.findById(id);
        if (optPost.isEmpty()) return ResponseEntity.notFound().build();
        SkillSharingPost post = optPost.get();
        if (!post.getUserEmail().equals(userEmail)) return ResponseEntity.status(403).body("Forbidden");

        post.setTitle(title);
        post.setDescription(description);
        post.setCategory(category);
        post.setTags(tags);

        if (file != null && !file.isEmpty()) {
            String uploadDir = "C:/Users/Shevon/Downloads/authdemo/uploads";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            String filePath = uploadDir + "/" + file.getOriginalFilename();
            file.transferTo(new File(filePath));
            post.setFilePath(filePath);
            post.setFileType(file.getContentType());
        }

        return ResponseEntity.ok(postService.save(post));
    }

    // DELETE (Authenticated user, only their own post)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePost(
            @PathVariable String id,
            @RequestHeader("Authorization") String authHeader
    ) {
        String userEmail = jwtUtil.extractEmail(authHeader.substring(7));
        Optional<SkillSharingPost> optPost = postService.findById(id);
        if (optPost.isEmpty()) return ResponseEntity.notFound().build();
        SkillSharingPost post = optPost.get();
        if (!post.getUserEmail().equals(userEmail)) return ResponseEntity.status(403).body("Forbidden");

        postService.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }
}
