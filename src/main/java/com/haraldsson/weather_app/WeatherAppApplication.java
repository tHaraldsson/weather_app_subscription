package com.haraldsson.weather_app;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Base64;

@SpringBootApplication
@EnableScheduling
@EnableRabbit
public class WeatherAppApplication {

	public static void main(String[] args) {SpringApplication.run(WeatherAppApplication.class, args);}

}
