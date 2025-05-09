// src/main/java/com/example/authdemo/model/Purchase.java
package com.example.authdemo.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "purchases")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Purchase {
    @Id
    private String id;
    private String courseId;
    private String userEmail;
    private String paymentIntentId; // Stripe payment ID
    private double amount;
    private Date purchaseDate;
    private boolean completed;
}
