package com.haraldsson.weather_app.dto;

public record SubscriptionRequest(
        String city,
        String timeOfDay,
        String frequency
) {}
