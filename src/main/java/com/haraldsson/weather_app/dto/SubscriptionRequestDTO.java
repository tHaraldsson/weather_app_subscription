package com.haraldsson.weather_app.dto;

public record SubscriptionRequestDTO(
        String city,
        String timeOfDay,
        String frequency
) {}
