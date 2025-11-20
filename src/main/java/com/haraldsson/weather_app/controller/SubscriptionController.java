package com.haraldsson.weather_app.controller;

import com.haraldsson.weather_app.config.JwtUtil;
import com.haraldsson.weather_app.dto.CityResponseDTO;
import com.haraldsson.weather_app.dto.SubscriptionRequestDTO;
import com.haraldsson.weather_app.dto.SubscriptionResponseDTO;
import com.haraldsson.weather_app.model.Subscription;
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

    // returnerar subscrip
    @GetMapping("/city")
    public ResponseEntity<CityResponseDTO> getUserCity(
            @RequestHeader("Authorization") String token) {

        UUID userId = extractUserId(token);
        Subscription subscription = subscriptionService.getSubscriptionForUser(userId);

        CityResponseDTO response = new CityResponseDTO(
                subscription.getCity(),
                userId.toString()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<SubscriptionResponseDTO> create(
            @RequestHeader("Authorization") String token,
            @RequestBody SubscriptionRequestDTO request) {

        UUID userId = extractUserId(token);
        return ResponseEntity.ok(subscriptionService.createOrUpdate(userId, request));
    }

    @GetMapping("/my")
    public ResponseEntity<SubscriptionResponseDTO> getMySubscription(
            @RequestHeader("Authorization") String token) {

        UUID userId = extractUserId(token);
        return ResponseEntity.ok(subscriptionService.getForUser(userId));
    }

    @PutMapping("/update")
    public ResponseEntity<SubscriptionResponseDTO> update(
            @RequestHeader("Authorization") String token,
            @RequestBody SubscriptionRequestDTO request) {

        UUID userId = extractUserId(token);
        return ResponseEntity.ok(subscriptionService.createOrUpdate(userId, request));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(
            @RequestHeader("Authorization") String token) {

        UUID userId = extractUserId(token);
        subscriptionService.deleteByUserId(userId);
        return ResponseEntity.noContent().build();
    }
}
