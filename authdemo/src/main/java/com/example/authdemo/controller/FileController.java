package com.example.authdemo.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
public class FileController {

    private static final String UPLOAD_DIR = "C:/Users/Shevon/Downloads/authdemo/uploads";
    
    private static final Map<String, MediaType> CONTENT_TYPES = new HashMap<>();
    static {
        // Image types
        CONTENT_TYPES.put("jpg", MediaType.IMAGE_JPEG);
        CONTENT_TYPES.put("jpeg", MediaType.IMAGE_JPEG);
        CONTENT_TYPES.put("png", MediaType.IMAGE_PNG);
        CONTENT_TYPES.put("gif", MediaType.IMAGE_GIF);
        // Document types
        CONTENT_TYPES.put("pdf", MediaType.APPLICATION_PDF);
        // Default
        CONTENT_TYPES.put("default", MediaType.APPLICATION_OCTET_STREAM);
    }

    @GetMapping("/api/file/{filename}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path path = Paths.get(UPLOAD_DIR + "/" + filename);
            Resource resource = new FileSystemResource(path.toFile());
            
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            // Determine content type based on file extension
            String extension = getExtension(filename);
            MediaType contentType = CONTENT_TYPES.getOrDefault(extension, CONTENT_TYPES.get("default"));
            
            return ResponseEntity.ok()
                .contentType(contentType)
                .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    private String getExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return filename.substring(lastDotIndex + 1).toLowerCase();
        }
        return "default";
    }
}
