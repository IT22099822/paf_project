//C:\Users\Shevon\Downloads\authdemo\src\main\java\com\example\authdemo\controller\UserProfileController.java
package com.example.authdemo.controller;

import com.example.authdemo.model.UserProfile;
import com.example.authdemo.model.SkillSharingPost;
import com.example.authdemo.model.Course;
import com.example.authdemo.model.Group;
import com.example.authdemo.service.UserProfileService;
import com.example.authdemo.service.SkillSharingPostService;
import com.example.authdemo.service.CourseService;
import com.example.authdemo.service.GroupService;
import com.example.authdemo.service.PurchaseService;
import com.example.authdemo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService profileService;
    private final SkillSharingPostService postService;
    private final GroupService groupService;
    private final CourseService courseService; 
    private final PurchaseService purchaseService;
    private final JwtUtil jwtUtil;

    // CREATE (JWT required)
    @PostMapping("/create")
    public ResponseEntity<?> createProfile(
            @RequestParam String name,
            @RequestParam String bio,
            @RequestParam String location,
            @RequestParam List<String> skills,
            @RequestParam List<String> socialLinks,
            @RequestParam(required = false) MultipartFile profilePicture,
            @RequestHeader("Authorization") String authHeader
    ) throws IOException {
        String userEmail = jwtUtil.extractEmail(authHeader.substring(7));
        if (profileService.findByUserEmail(userEmail).isPresent())
            return ResponseEntity.badRequest().body("Profile already exists");

        UserProfile.UserProfileBuilder builder = UserProfile.builder()
                .userEmail(userEmail)
                .name(name)
                .bio(bio)
                .location(location)
                .skills(skills)
                .socialLinks(socialLinks);

        if (profilePicture != null && !profilePicture.isEmpty()) {
            String uploadDir = "C:/Users/Shevon/Downloads/authdemo/uploads";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            String picturePath = uploadDir + "/" + profilePicture.getOriginalFilename();
            profilePicture.transferTo(new File(picturePath));
            builder.profilePicturePath(picturePath);
            builder.profilePictureType(profilePicture.getContentType());
        }

        return ResponseEntity.ok(profileService.save(builder.build()));
    }

    // READ (public)
    @GetMapping("/user/{userEmail}")
    public ResponseEntity<?> getProfile(@PathVariable String userEmail) {
        Optional<UserProfile> optProfile = profileService.findByUserEmail(userEmail);
        if (optProfile.isEmpty()) return ResponseEntity.notFound().build();

        UserProfile profile = optProfile.get();

        // Get posts by this user
        List<SkillSharingPost> posts = postService.findByUserEmail(userEmail);

        // Get groups created by this user
        List<Group> groupsCreated = groupService.findByAdminEmail(userEmail);

        // Get groups joined by this user
        List<Group> allGroups = groupService.findAll();
        List<Group> groupsJoined = new ArrayList<>();
        for (Group g : allGroups) {
            if (g.getMemberEmails().contains(userEmail)) {
                groupsJoined.add(g);
            }

        // Get courses purchased by this user
        List<Course> purchasedCourses = purchaseService.findCoursesByPurchaserEmail(userEmail);

        Map<String, Object> result = new HashMap<>();
        result.put("profile", profile);
        result.put("posts", posts);
        result.put("groupsCreated", groupsCreated);
        result.put("groupsJoined", groupsJoined);
        result.put("purchasedCourses", purchasedCourses);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("profile", profile);
        result.put("posts", posts);
        result.put("groupsCreated", groupsCreated);
        result.put("groupsJoined", groupsJoined);

        return ResponseEntity.ok(result);
    }

    // UPDATE (JWT required)
    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(
            @RequestParam String name,
            @RequestParam String bio,
            @RequestParam String location,
            @RequestParam List<String> skills,
            @RequestParam List<String> socialLinks,
            @RequestParam(required = false) MultipartFile profilePicture,
            @RequestHeader("Authorization") String authHeader
    ) throws IOException {
        String userEmail = jwtUtil.extractEmail(authHeader.substring(7));
        Optional<UserProfile> optProfile = profileService.findByUserEmail(userEmail);
        if (optProfile.isEmpty()) return ResponseEntity.notFound().build();

        UserProfile profile = optProfile.get();
        profile.setName(name);
        profile.setBio(bio);
        profile.setLocation(location);
        profile.setSkills(skills);
        profile.setSocialLinks(socialLinks);

        if (profilePicture != null && !profilePicture.isEmpty()) {
            String uploadDir = "C:/Users/Shevon/Downloads/authdemo/uploads";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            String picturePath = uploadDir + "/" + profilePicture.getOriginalFilename();
            profilePicture.transferTo(new File(picturePath));
            profile.setProfilePicturePath(picturePath);
            profile.setProfilePictureType(profilePicture.getContentType());
        }

        return ResponseEntity.ok(profileService.save(profile));
    }

    // DELETE (JWT required)
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteProfile(@RequestHeader("Authorization") String authHeader) {
        String userEmail = jwtUtil.extractEmail(authHeader.substring(7));
        Optional<UserProfile> optProfile = profileService.findByUserEmail(userEmail);
        if (optProfile.isEmpty()) return ResponseEntity.notFound().build();
        profileService.deleteById(optProfile.get().getId());
        return ResponseEntity.ok("Profile deleted");
    }

    // READ ALL (public)
    @GetMapping("/all")
    public List<UserProfile> getAllProfiles() {
        return profileService.findAll();
    }
}
