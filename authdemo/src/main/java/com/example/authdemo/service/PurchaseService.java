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

    public List<Purchase> findAll() {
        return purchaseRepo.findAll();
    }

    public Purchase findByPaymentIntentId(String paymentIntentId) {
        return purchaseRepo.findByPaymentIntentId(paymentIntentId).orElse(null);
    }

    public Purchase findLatestPendingPurchase(String userEmail, String courseId) {
        return purchaseRepo.findFirstByUserEmailAndCourseIdAndCompletedOrderByPurchaseDateDesc(userEmail, courseId, false).orElse(null);
    }

    // NEW: Find by sessionId and userEmail
    public Purchase findBySessionIdAndUserEmail(String sessionId, String userEmail) {
        return purchaseRepo.findBySessionIdAndUserEmail(sessionId, userEmail).orElse(null);
    }
}
