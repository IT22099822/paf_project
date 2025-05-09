//C:\Users\Shevon\Downloads\authdemo\src\main\java\com\example\authdemo\controller\PostFeedbackController.java
package com.example.authdemo.controller;

import com.example.authdemo.model.PostFeedback;
import com.example.authdemo.service.PostFeedbackService;
import com.example.authdemo.service.SkillSharingPostService;
import com.example.authdemo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class PostFeedbackController {
    private final PostFeedbackService feedbackService;
    private final SkillSharingPostService postService;
    private final JwtUtil jwtUtil;

    // CREATE (JWT required)
    @PostMapping("/create")
    public ResponseEntity<?> createFeedback(
            @RequestParam String postId,
            @RequestParam String comment,
            @RequestParam Integer rating,
            @RequestParam(required = false) MultipartFile picture,
            @RequestHeader("Authorization") String authHeader) throws IOException {
        String userEmail = jwtUtil.extractEmail(authHeader.substring(7));
        // Check post exists
        if (postService.findById(postId).isEmpty())
            return ResponseEntity.badRequest().body("Post not found");

        PostFeedback.PostFeedbackBuilder builder = PostFeedback.builder()
                .postId(postId)
                .userEmail(userEmail)
                .comment(comment)
                .rating(rating);

        if (picture != null && !picture.isEmpty()) {
            String uploadDir = "C:/Users/Shevon/Downloads/authdemo/uploads";
            File dir = new File(uploadDir);
            if (!dir.exists())
                dir.mkdirs();
            String picturePath = uploadDir + "/" + picture.getOriginalFilename();
            picture.transferTo(new File(picturePath));
            builder.picturePath(picturePath);
            builder.pictureType(picture.getContentType());
        }

        return ResponseEntity.ok(feedbackService.save(builder.build()));
    }

    // READ (public)
    @GetMapping("/post/{postId}")
    public List<PostFeedback> getFeedbackForPost(@PathVariable String postId) {
        return feedbackService.findByPostId(postId);
    }

    // UPDATE (JWT, only feedback owner)
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateFeedback(
            @PathVariable String id,
            @RequestParam String comment,
            @RequestParam Integer rating,
            @RequestParam(required = false) MultipartFile picture,
            @RequestHeader("Authorization") String authHeader) throws IOException {
        String userEmail = jwtUtil.extractEmail(authHeader.substring(7));

        // Check if feedback exists
        Optional<PostFeedback> opt = feedbackService.findById(id);
        if (opt.isEmpty())
            return ResponseEntity.notFound().build();

        PostFeedback feedback = opt.get();

        if (!feedback.getUserEmail().equals(userEmail))
            return ResponseEntity.status(403).body("Forbidden");

        feedback.setComment(comment);
        feedback.setRating(rating);

        if (picture != null && !picture.isEmpty()) {
            String uploadDir = "C:/Users/Shevon/Downloads/authdemo/uploads";
            File dir = new File(uploadDir);
            if (!dir.exists())
                dir.mkdirs();
            String picturePath = uploadDir + "/" + picture.getOriginalFilename();
            picture.transferTo(new File(picturePath));
            feedback.setPicturePath(picturePath);
            feedback.setPictureType(picture.getContentType());
        }

        return ResponseEntity.ok(feedbackService.save(feedback));
    }

    // DELETE (JWT, only feedback owner)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFeedback(
            @PathVariable String id,
            @RequestHeader("Authorization") String authHeader) {
        String userEmail = jwtUtil.extractEmail(authHeader.substring(7));
        Optional<PostFeedback> opt = feedbackService.findById(id);
        if (opt.isEmpty())
            return ResponseEntity.notFound().build();
        if (!opt.get().getUserEmail().equals(userEmail))
            return ResponseEntity.status(403).body("Forbidden");
        feedbackService.deleteById(id);
        return ResponseEntity.ok("Feedback deleted");
    }
}
