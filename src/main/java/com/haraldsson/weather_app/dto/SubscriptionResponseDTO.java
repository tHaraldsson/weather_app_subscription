package com.haraldsson.weather_app.dto;

import java.util.UUID;

public record SubscriptionResponseDTO(
        Long id,
        UUID userId,
        String city,
        String timeOfDay,
        boolean active
) {}
