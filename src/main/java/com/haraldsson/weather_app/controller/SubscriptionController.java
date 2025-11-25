package com.haraldsson.weather_app.controller;

import com.haraldsson.weather_app.config.JwtUtil;
import com.haraldsson.weather_app.dto.CityResponseDTO;
import com.haraldsson.weather_app.dto.SubscriptionRequestDTO;
import com.haraldsson.weather_app.dto.SubscriptionResponseDTO;
import com.haraldsson.weather_app.model.Subscription;
import com.haraldsson.weather_app.service.SubscriptionService;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
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

    // returnerar city och userid
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
            @RequestHeader("Authorization") String token,
            @RequestBody SubscriptionRequestDTO request) {

        UUID userId = jwtUtil.extractUserId(token);
        return ResponseEntity.ok(subscriptionService.createOrUpdate(userId, request));
    }

    // Hämtar den aktuella subscription för en user
    @GetMapping("/my")
    public ResponseEntity<SubscriptionResponseDTO> getMySubscription(
            @RequestHeader("Authorization") String token) {

        UUID userId = jwtUtil.extractUserId(token);
        return ResponseEntity.ok(subscriptionService.getForUser(userId));
    }

    /**
     * Create/Update i samma endpoint
     */
//    @PutMapping("/update")
//    public ResponseEntity<SubscriptionResponseDTO> update(
//            @RequestHeader("Authorization") String token,
//            @RequestBody SubscriptionRequestDTO request) {
//
//        UUID userId = jwtUtil.extractUserId(token);
//        return ResponseEntity.ok(subscriptionService.createOrUpdate(userId, request));
//    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(
            @RequestHeader("Authorization") String token) {

        UUID userId = jwtUtil.extractUserId(token);
        subscriptionService.deleteByUserId(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of(
                "status", "OK",
                "message", "Backend is running!",
                "timestamp", java.time.LocalDateTime.now().toString()
        );
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
                "status", "UP",
                "service", "Weather App API",
                "version", "1.0.0"
        );
    }

}
