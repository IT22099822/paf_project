package com.example.authdemo.repository;

import com.example.authdemo.model.Purchase;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface PurchaseRepository extends MongoRepository<Purchase, String> {
    List<Purchase> findByUserEmailAndCompleted(String userEmail, boolean completed);
    Optional<Purchase> findByPaymentIntentId(String paymentIntentId);
    Optional<Purchase> findFirstByUserEmailAndCourseIdAndCompletedOrderByPurchaseDateDesc(String userEmail, String courseId, boolean completed);

    // NEW: Find by sessionId and userEmail
    Optional<Purchase> findBySessionIdAndUserEmail(String sessionId, String userEmail);
}
