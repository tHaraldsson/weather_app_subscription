package com.haraldsson.weather_app.service;

import com.haraldsson.weather_app.config.RabbitConfig;
import com.haraldsson.weather_app.dto.CityResponseDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionProducer {

    private final RabbitTemplate rabbitTemplate;

    public SubscriptionProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendResponse(CityResponseDTO dto) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                RabbitConfig.ROUTING_KEY,
                dto
        );
        System.out.println("Skickade DTO till Johan: " + dto);
    }
}

