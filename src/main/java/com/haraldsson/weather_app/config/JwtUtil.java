package com.haraldsson.weather_app.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.util.UUID;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;// här måste vi sätta in secret key

    public UUID extractUserId(String token) {
        token = token.replace("Bearer ", "");

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();


        return UUID.fromString(claims.get("userId", String.class));
    }
}
