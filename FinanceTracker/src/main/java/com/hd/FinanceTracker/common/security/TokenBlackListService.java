package com.hd.FinanceTracker.common.security;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class TokenBlackListService {
    private final StringRedisTemplate stringRedisTemplate;
    public void blacklistToken(String token, long expirationMs) {
        stringRedisTemplate.opsForValue().set(token,"true", expirationMs, TimeUnit.MILLISECONDS);
    }
    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(token));
    }
}
