package com.haraldsson.weather_app.dto;

import java.util.UUID;

public record SubscriptionResponse(
        Long id,
        UUID userId,
        String city,
        String timeOfDay,
        String frequency,
        boolean active
) {}
