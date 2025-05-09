// src/main/java/com/example/authdemo/service/PurchaseService.java
package com.example.authdemo.service;

import com.example.authdemo.model.Course;
import com.example.authdemo.model.Purchase;
import com.example.authdemo.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseService {
    private final PurchaseRepository purchaseRepo;
    private final CourseService courseService;

    public Purchase save(Purchase purchase) { return purchaseRepo.save(purchase); }
    
    public List<Course> findCoursesByPurchaserEmail(String userEmail) {
        List<Purchase> purchases = purchaseRepo.findByUserEmailAndCompleted(userEmail, true);
        List<Course> courses = new ArrayList<>();
        
        for (Purchase purchase : purchases) {
            courseService.findById(purchase.getCourseId())
                .ifPresent(courses::add);
        }
        
        return courses;
    }
}
