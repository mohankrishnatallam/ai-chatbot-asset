package com.cx.asset.service;

import com.cx.asset.config.MongoChartsProperties;
import com.cx.asset.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class ChartsEmbedTokenService {

    private final MongoChartsProperties chartsProperties;
    private final UserRepository userRepository;

    public ChartsEmbedTokenService(MongoChartsProperties chartsProperties, UserRepository userRepository) {
        this.chartsProperties = chartsProperties;
        this.userRepository = userRepository;
    }

    public String createToken(String userId) {
        String normalizedUserId = userId == null ? "" : userId.trim();
        if (normalizedUserId.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "X-User-Id header is required.");
        }

        if (!userRepository.existsById(normalizedUserId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not logged in or was not found.");
        }

        if (!chartsProperties.isTokenSigningConfigured()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    chartsProperties.missingConfigurationMessage());
        }

        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(chartsProperties.getTokenTtlSeconds());
        String audience = chartsProperties.getTokenAudience();
        SecretKey key = Keys.hmacShaKeyFor(chartsProperties.getEmbedSecret().getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject(normalizedUserId)
                .issuer(audience)
                .audience().add(audience).and()
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(key)
                .compact();
    }
}
