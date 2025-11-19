package com.haraldsson.weather_app.repository;

import com.haraldsson.weather_app.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByUserId(UUID userId);

    List<Subscription> findByTimeOfDayAndActive(String timeOfDay, boolean active);
}
