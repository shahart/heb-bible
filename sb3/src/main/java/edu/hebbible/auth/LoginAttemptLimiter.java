package edu.hebbible.auth;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Component
class LoginAttemptLimiter {

    static final int MAX_ATTEMPTS = 3;
    static final Duration ATTEMPT_WINDOW = Duration.ofMinutes(5);

    private static final String KEY_PREFIX = "hebbible:auth:login-attempts:";
    private static final RedisScript<Long> ACQUIRE_ATTEMPT = RedisScript.of("""
            local redis_time = redis.call('TIME')
            local now = redis_time[1] * 1000 + math.floor(redis_time[2] / 1000)
            local cutoff = now - tonumber(ARGV[1])

            redis.call('ZREMRANGEBYSCORE', KEYS[1], '-inf', cutoff)
            if redis.call('ZCARD', KEYS[1]) >= tonumber(ARGV[2]) then
                return 0
            end

            redis.call('ZADD', KEYS[1], now, ARGV[3])
            redis.call('PEXPIRE', KEYS[1], ARGV[1])
            return 1
            """, Long.class);

    private final StringRedisTemplate redis;

    LoginAttemptLimiter(StringRedisTemplate redis) {
        this.redis = redis;
    }

    boolean tryAcquire(String email) {
        Long result = redis.execute(
                ACQUIRE_ATTEMPT,
                List.of(key(email)),
                Long.toString(ATTEMPT_WINDOW.toMillis()),
                Integer.toString(MAX_ATTEMPTS),
                UUID.randomUUID().toString());
        return Long.valueOf(1).equals(result);
    }

    void recordSuccess(String email) {
        redis.delete(key(email));
    }

    private String key(String email) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        return KEY_PREFIX + sha256(normalizedEmail);
    }

    private String sha256(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
