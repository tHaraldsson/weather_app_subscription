package com.haraldsson.weather_app.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;


import java.util.UUID;

@Component
public class JwtUtil {

    private final String SECRET = "";// här måste vi sätta in secret key

    public UUID extractUserId(String token) {
        token = token.replace("Bearer ", "");

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();


        return UUID.fromString(claims.get("userId", String.class));
    }
}
