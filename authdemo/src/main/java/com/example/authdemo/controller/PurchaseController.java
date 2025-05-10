package com.example.authdemo.controller;

import com.example.authdemo.model.Course;
import com.example.authdemo.model.Purchase;
import com.example.authdemo.service.CourseService;
import com.example.authdemo.service.PurchaseService;
import com.example.authdemo.util.JwtUtil;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/purchase")
@RequiredArgsConstructor
public class PurchaseController {
    private final JwtUtil jwtUtil;
    private final CourseService courseService;
    private final PurchaseService purchaseService;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    // List all purchases (for testing)
    @GetMapping("/all")
    public ResponseEntity<?> getAllPurchases() {
        return ResponseEntity.ok(purchaseService.findAll());
    }

    @PostMapping("/confirm-payment")
    public ResponseEntity<?> confirmPayment(
            @RequestParam String sessionId,
            @RequestHeader("Authorization") String authHeader
    ) throws Exception {
        String userEmail = jwtUtil.extractEmail(authHeader.substring(7));
        Stripe.apiKey = stripeSecretKey;

        // Retrieve the session from Stripe
        Session session = Session.retrieve(sessionId);

        // Check if payment was successful
        if ("complete".equals(session.getStatus())) {
            // Find the purchase by sessionId and userEmail and set completed=true
            Purchase purchase = purchaseService.findBySessionIdAndUserEmail(sessionId, userEmail);
            if (purchase != null) {
                purchase.setCompleted(true);
                purchase.setPaymentIntentId(session.getPaymentIntent());
                purchaseService.save(purchase);
                return ResponseEntity.ok("Payment confirmed");
            }
        }
        return ResponseEntity.badRequest().body("Payment not completed or purchase not found");
    }

    @PostMapping("/create-checkout-session")
    public ResponseEntity<?> createCheckoutSession(
            @RequestBody Map<String, String> body,
            @RequestHeader("Authorization") String authHeader
    ) throws Exception {
        String courseId = body.get("courseId");
        String userEmail = jwtUtil.extractEmail(authHeader.substring(7));
        Optional<Course> courseOpt = courseService.findById(courseId);
        if (courseOpt.isEmpty()) return ResponseEntity.badRequest().body("Invalid course");

        Course course = courseOpt.get();

        Stripe.apiKey = stripeSecretKey;

        SessionCreateParams params =
            SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/purchase-success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:3000/purchase-cancel")
                .addLineItem(
                    SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd")
                                .setUnitAmount((long)(course.getPrice() * 100)) // Stripe expects cents
                                .setProductData(
                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(course.getTitle())
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .putMetadata("userEmail", userEmail)
                .putMetadata("courseId", courseId)
                .build();

        Session session = Session.create(params);

        // Create a Purchase record with completed=false and store sessionId!
        Purchase purchase = Purchase.builder()
                .courseId(courseId)
                .userEmail(userEmail)
                .paymentIntentId(session.getPaymentIntent())
                .sessionId(session.getId()) // <-- ADD THIS LINE
                .amount(course.getPrice())
                .purchaseDate(new Date())
                .completed(false)
                .build();
        purchaseService.save(purchase);

        return ResponseEntity.ok(Map.of("url", session.getUrl()));
    }
}
