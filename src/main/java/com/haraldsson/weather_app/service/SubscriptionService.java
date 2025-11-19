package com.haraldsson.weather_app.service;

import com.haraldsson.weather_app.config.RabbitConfig;
import com.haraldsson.weather_app.dto.SubscriptionRequest;
import com.haraldsson.weather_app.dto.SubscriptionResponse;
import com.haraldsson.weather_app.model.Subscription;
import com.haraldsson.weather_app.repository.SubscriptionRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class SubscriptionService {

    private final SubscriptionRepository repository;
    private final RabbitTemplate rabbitTemplate;
    @Autowired
    public SubscriptionService(SubscriptionRepository repository, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
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

        sendSubscriptionEvent(s);

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
    public void deleteByUserId(UUID userId) {
        Subscription subscription = repository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("No subscription found for user"));
        subscription.setActive(false);
        repository.save(subscription);
    }

    /**
    * Gör om subscription till DTO
     */
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

    /**
     * Skickar ett meddelande om subscription till RabbitMQ.
     */
    private void sendSubscriptionEvent(Subscription s) {
        Map<String, Object> payload = Map.of(
                "userId", s.getUserId().toString(),
                "city", s.getCity(),
                "timeOfDay", s.getTimeOfDay()
        );

        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, RabbitConfig.ROUTING_KEY, payload);
    }

    /**
    * Skickar alla due kl 7
    * Hämtar subscriptions kl 7
     */
    @Scheduled(cron = "0 0 7 * * *")
    public void publishDailySubscriptions() {
        List<Subscription> subscriptions = repository.findByTimeOfDayAndActive("07:00", true);
        subscriptions.forEach(this::sendSubscriptionEvent);
    }

}
