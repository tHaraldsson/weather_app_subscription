package com.haraldsson.weather_app.service;

import com.haraldsson.weather_app.dto.SubscriptionRequest;
import com.haraldsson.weather_app.dto.SubscriptionResponse;
import com.haraldsson.weather_app.model.Subscription;
import com.haraldsson.weather_app.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SubscriptionService {

    private final SubscriptionRepository repository;

    @Autowired
    public SubscriptionService(SubscriptionRepository repository) {
        this.repository = repository;
    }

    /**
     * Skapar eller uppdaterar subscription för en user.
     */
    public SubscriptionResponse createOrUpdate(UUID userId, SubscriptionRequest request) {
        Optional<Subscription> optional = repository.findByUserId(userId);

        Subscription s;
        if (optional.isPresent()) {
            s = optional.get();
            s.setCity(request.city());
            s.setTimeOfDay(request.timeOfDay());
            s.setFrequency(request.frequency());
            s.setActive(true);
        } else {
            s = new Subscription(
                    userId,
                    request.city(),
                    request.timeOfDay(),
                    request.frequency()
            );
        }

        repository.save(s);
        return toResponse(s);
    }

    /**
     * Hämtar subscription för en user
     */
    public SubscriptionResponse getForUser(UUID userId) {
        Subscription s = repository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("No subscription found for user"));

        return toResponse(s);
    }

    /**
     * Inaktiverar subscription
     */
    public void delete(Long subscriptionId, UUID userId) {
        Subscription s = repository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        if (!s.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        s.setActive(false);
        repository.save(s);
    }

    private SubscriptionResponse toResponse(Subscription s) {
        return new SubscriptionResponse(
                s.getId(),
                s.getUserId(),
                s.getCity(),
                s.getTimeOfDay(),
                s.getFrequency(),
                s.isActive()
        );
    }
}
