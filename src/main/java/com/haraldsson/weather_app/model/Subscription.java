package com.haraldsson.weather_app.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false, unique = true)
    UUID userId;
    @Column(nullable = false)
    String city;
    @Column(nullable = false)
    String timeOfDay;

    boolean active;

    public Subscription() {}

    public Subscription(UUID userId, String city, String timeOfDay) {
        this.userId = userId;
        this.city = city;
        this.timeOfDay = timeOfDay;
        this.active = true;
    }



    public UUID getUserId() {
        return userId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(String timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
