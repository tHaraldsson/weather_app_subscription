package com.haraldsson.weather_app.service;

import com.haraldsson.weather_app.config.RabbitConfig;
import com.haraldsson.weather_app.dto.CityResponseDTO;
import com.haraldsson.weather_app.dto.SubscriptionRequestDTO;
import com.haraldsson.weather_app.dto.SubscriptionResponseDTO;
import com.haraldsson.weather_app.model.Subscription;
import com.haraldsson.weather_app.repository.SubscriptionRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@EnableScheduling
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
    public SubscriptionResponseDTO createOrUpdate(UUID userId, SubscriptionRequestDTO request) {
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
    public SubscriptionResponseDTO getForUser(UUID userId) {
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
    private SubscriptionResponseDTO toResponse(Subscription s) {
        return new SubscriptionResponseDTO(
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
        try {
            CityResponseDTO notification = new CityResponseDTO(
                    s.getUserId().toString(),
                    s.getCity()
            );

            System.out.println("Skickar till Johan: " + notification);

            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, RabbitConfig.ROUTING_KEY, notification);

        } catch (Exception e) {
            System.err.println("RabbitMQ failed: " + e.getMessage());
        }
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

    /**
    * Hämtar en subscription entity
     */
    public Subscription getSubscriptionForUser(UUID userId) {
        return repository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("No subscription found for user"));
    }

    /**
     * Hämtar bara stad för en användare
     */
    public String getUserCity(UUID userId) {
        Subscription subscription = getSubscriptionForUser(userId);
        return subscription.getCity();
    }

}
