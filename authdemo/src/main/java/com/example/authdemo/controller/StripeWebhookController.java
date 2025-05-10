//C:\Users\Shevon\Downloads\authdemo\src\main\java\com\example\authdemo\controller\StripeWebhookController.java
package com.example.authdemo.controller;

import com.example.authdemo.model.Purchase;
import com.example.authdemo.service.PurchaseService;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/stripe")
@RequiredArgsConstructor
public class StripeWebhookController {

    private final PurchaseService purchaseService;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    // Set this in Stripe dashboard > Developers > Webhooks
    @Value("${stripe.webhook.secret:}")
    private String endpointSecret;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
                                                     @RequestHeader("Stripe-Signature") String sigHeader) {
        Stripe.apiKey = stripeSecretKey;

        Event event;
        try {
            if (endpointSecret != null && !endpointSecret.isEmpty()) {
                event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            } else {
                event = Event.GSON.fromJson(payload, Event.class);
            }
        } catch (SignatureVerificationException e) {
            return ResponseEntity.badRequest().body("Invalid signature");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Webhook error: " + e.getMessage());
        }

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session != null) {
                String paymentIntentId = session.getPaymentIntent();
                // Efficiently find the purchase and mark as completed
                Purchase purchase = purchaseService.findByPaymentIntentId(paymentIntentId);
                if (purchase != null) {
                    purchase.setCompleted(true);
                    purchase.setPurchaseDate(new Date());
                    purchaseService.save(purchase);
                }
            }
        }

        return ResponseEntity.ok("Received");
    }
}
