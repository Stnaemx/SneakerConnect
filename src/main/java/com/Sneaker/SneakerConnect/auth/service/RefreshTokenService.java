package com.Sneaker.SneakerConnect.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    public void createAndStoreRefreshTokenWithExpiration(String UUID, String userEmail) {
        if(UUID == null || UUID.isEmpty()) {
            throw new IllegalArgumentException("UUID must not be null or empty");
        }

        // store refresh token with a 30 day TTL
        try {
            Duration duration = Duration.ofDays(30L);
            redisTemplate.opsForValue().set(UUID, userEmail, duration); // token valid for 30 days

            // calculate the timestamp 30 days ahead in Epoch seconds
            long timestamp30DaysAhead = Instant.now()
                    .plus(30, ChronoUnit.DAYS)
                    .getEpochSecond();

            // return refresh token expiration time
//            return String.valueOf(timestamp30DaysAhead);
        } catch (RedisSystemException e) {
            throw new RuntimeException("Failed to store refresh token", e);
        }
    }

    // not used as it requires a redis call
    public boolean keyExist(String refreshToken) {
        if(refreshToken == null || refreshToken.isEmpty()) {
            return false;
        }
        return Boolean.TRUE.equals(redisTemplate.hasKey(refreshToken));
    }

    public String getUserEmail(String UUID) {
        return redisTemplate.opsForValue().get(UUID);
    }

    public String getExpireTime(String UUID) {
        long currentTime = Instant.now().getEpochSecond();
        long expireTime = redisTemplate.getExpire(UUID);
        return String.valueOf(currentTime + expireTime);
    }
}
