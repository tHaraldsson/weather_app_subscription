package com.haraldsson.weather_app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        log.info("SUBSCRIPTION HEALTH CHECK OK");
        return ResponseEntity.ok().body("SUB OK");
    }
}
