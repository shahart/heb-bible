package edu.hebbible.auth;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@ConditionalOnProperty(
        name = "hebbible.auth.rate-limit.storage",
        havingValue = "redis",
        matchIfMissing = true)
class RedisLoginAttemptLimiter implements LoginAttemptLimiter {

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

    RedisLoginAttemptLimiter(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public boolean tryAcquire(String email) {
        Long result = redis.execute(
                ACQUIRE_ATTEMPT,
                List.of(LoginAttemptLimiter.key(email)),
                Long.toString(ATTEMPT_WINDOW.toMillis()),
                Integer.toString(MAX_ATTEMPTS),
                UUID.randomUUID().toString());
        return Long.valueOf(1).equals(result);
    }

    @Override
    public void recordSuccess(String email) {
        redis.delete(LoginAttemptLimiter.key(email));
    }
}
