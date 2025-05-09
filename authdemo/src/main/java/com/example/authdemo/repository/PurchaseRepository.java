// src/main/java/com/example/authdemo/repository/PurchaseRepository.java
package com.example.authdemo.repository;

import com.example.authdemo.model.Purchase;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PurchaseRepository extends MongoRepository<Purchase, String> {
    List<Purchase> findByUserEmailAndCompleted(String userEmail, boolean completed);
}
