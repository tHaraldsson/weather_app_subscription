package com.haraldsson.weather_app.controller;

import com.haraldsson.weather_app.config.JwtUtil;
import com.haraldsson.weather_app.dto.SubscriptionRequest;
import com.haraldsson.weather_app.dto.SubscriptionResponse;
import com.haraldsson.weather_app.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final JwtUtil jwtUtil;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService, JwtUtil jwtUtil) {
        this.subscriptionService = subscriptionService;
        this.jwtUtil = jwtUtil;
    }

    private UUID extractUserId(String header) {
        return jwtUtil.extractUserId(header);
    }

    @PostMapping("/create")
    public ResponseEntity<SubscriptionResponse> create(
            @RequestHeader("Authorization") String token,
            @RequestBody SubscriptionRequest request) {

        UUID userId = extractUserId(token);
        return ResponseEntity.ok(subscriptionService.createOrUpdate(userId, request));
    }

    @GetMapping("/my")
    public ResponseEntity<SubscriptionResponse> getMySubscription(
            @RequestHeader("Authorization") String token) {

        UUID userId = extractUserId(token);
        return ResponseEntity.ok(subscriptionService.getForUser(userId));
    }

    @PutMapping("/update")
    public ResponseEntity<SubscriptionResponse> update(
            @RequestHeader("Authorization") String token,
            @RequestBody SubscriptionRequest request) {

        UUID userId = extractUserId(token);
        return ResponseEntity.ok(subscriptionService.createOrUpdate(userId, request));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(
            @RequestHeader("Authorization") String token) {

        UUID userId = extractUserId(token);
        SubscriptionResponse subscription = subscriptionService.getForUser(userId);
        subscriptionService.delete(subscription.id(), userId);
        return ResponseEntity.noContent().build();
    }
}
