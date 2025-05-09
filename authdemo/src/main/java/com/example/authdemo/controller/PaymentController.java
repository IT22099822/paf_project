package com.example.authdemo.controller;

import com.example.authdemo.service.CourseService;
import com.example.authdemo.service.PurchaseService;
import com.example.authdemo.util.JwtUtil;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;
import com.example.authdemo.model.Course;
import com.example.authdemo.model.Purchase;
import java.util.Date;


import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final CourseService courseService;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${stripe.public.key}")
    private String stripePublicKey;

    private final JwtUtil jwtUtil;

    private final PurchaseService purchaseService;


    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @PostMapping("/create-checkout-session/{courseId}")
    public ResponseEntity<?> createCheckoutSession(
            @PathVariable String courseId,
            @RequestHeader("Authorization") String authHeader
    ) throws StripeException {
        Course course = courseService.findById(courseId).orElseThrow();

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:3000/cancel")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(course.getTitle())
                                                                .build()
                                                )
                                                .setUnitAmount((long) (course.getPrice() * 100))
                                                .build()
                                )
                                .setQuantity(1L)
                                .build()
                )
                .build();

        Session session = Session.create(params);
        Map<String, String> response = new HashMap<>();
        response.put("id", session.getId());
        response.put("publicKey", stripePublicKey);
        return ResponseEntity.ok(response);
    }

    // In PaymentController.java, add this method to create a purchase record when payment succeeds
@PostMapping("/confirm-payment")
public ResponseEntity<?> confirmPayment(
        @RequestParam String sessionId,
        @RequestHeader("Authorization") String authHeader
) throws StripeException {
    String userEmail = jwtUtil.extractEmail(authHeader.substring(7));
    
    // Retrieve the session from Stripe
    Session session = Session.retrieve(sessionId);
    
    // Check if payment was successful
    if ("complete".equals(session.getStatus())) {
        // Extract courseId from metadata
        String courseId = session.getMetadata().get("courseId");
        
        // Create purchase record
        Purchase purchase = Purchase.builder()
                .courseId(courseId)
                .userEmail(userEmail)
                .paymentIntentId(session.getPaymentIntent())
                .amount(session.getAmountTotal() / 100.0) // Convert from cents
                .purchaseDate(new Date())
                .completed(true)
                .build();
        
        purchaseService.save(purchase);
        return ResponseEntity.ok("Payment confirmed");
    }
    
    return ResponseEntity.badRequest().body("Payment not completed");
}


    // Add webhook handler here (optional for basic flow)
}
