package com.haraldsson.weather_app.controller;

import com.haraldsson.weather_app.config.JwtUtil;
import com.haraldsson.weather_app.dto.CityResponseDTO;
import com.haraldsson.weather_app.dto.SubscriptionRequestDTO;
import com.haraldsson.weather_app.dto.SubscriptionResponseDTO;
import com.haraldsson.weather_app.model.Subscription;
import com.haraldsson.weather_app.service.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(SubscriptionController.class);

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService, JwtUtil jwtUtil) {
        this.subscriptionService = subscriptionService;
        this.jwtUtil = jwtUtil;
    }

    // returnerar city och userid -- behövs ej. skickas via messagebroker
    @GetMapping("/city")
    public ResponseEntity<CityResponseDTO> getUserCity(
            @RequestHeader("Authorization") String token) {

        UUID userId = jwtUtil.extractUserId(token);
        Subscription subscription = subscriptionService.getSubscriptionForUser(userId);

        CityResponseDTO response = new CityResponseDTO(
                subscription.getCity(),
                userId.toString()
        );
        return ResponseEntity.ok(response);
    }

    // skapar en subscription som är kopplad till en user
    @PostMapping("/create")
    public ResponseEntity<SubscriptionResponseDTO> create(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody SubscriptionRequestDTO request) {

        if (token == null || token.isEmpty()) {
            logger.info("TOKEN IS NULL OR EMPTY!!!" + token);
            return ResponseEntity.status(401).build();
        }

        UUID userId = jwtUtil.extractUserId(token);
        return ResponseEntity.ok(subscriptionService.createOrUpdate(userId, request));
    }

    // Hämtar den aktuella subscription för en user
    @GetMapping("/my")
    public ResponseEntity<SubscriptionResponseDTO> getMySubscription(
            @RequestHeader(value = "Authorization", required = false) String token) {

        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        UUID userId = jwtUtil.extractUserId(token);
        return ResponseEntity.ok(subscriptionService.getForUser(userId));
    }


    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(
            @RequestHeader("Authorization") String token) {

        UUID userId = jwtUtil.extractUserId(token);
        subscriptionService.deleteByUserId(userId);
        return ResponseEntity.noContent().build();
    }

}
