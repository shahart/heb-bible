package edu.hebbible.auth;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoginAttemptLimiterTest {

    @Test
    void returnsTheAtomicRedisScriptDecision() {
        StringRedisTemplate redis = mock(StringRedisTemplate.class);
        LoginAttemptLimiter limiter = new LoginAttemptLimiter(redis);
        when(redis.execute(any(RedisScript.class), anyList(), anyString(), anyString(), anyString()))
                .thenReturn(1L, 0L);

        assertTrue(limiter.tryAcquire("User@Example.com"));
        assertFalse(limiter.tryAcquire("user@example.com"));
    }

    @Test
    void successfulLoginDeletesTheNormalizedHashedAccountKey() {
        StringRedisTemplate redis = mock(StringRedisTemplate.class);
        LoginAttemptLimiter limiter = new LoginAttemptLimiter(redis);
        when(redis.execute(any(RedisScript.class), anyList(), anyString(), anyString(), anyString()))
                .thenReturn(1L);

        limiter.tryAcquire(" User@Example.com ");
        limiter.recordSuccess("user@example.com");

        verify(redis).delete(eq(
                "hebbible:auth:login-attempts:b4c9a289323b21a01c3e940f150eb9b8c542587f1abfd8f0e1cc1ffc5e475514"));
    }
}
