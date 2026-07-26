package edu.hebbible.auth;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginAttemptLimiterTest {

    @Test
    void allowsAttemptsAgainAfterFiveMinutes() {
        Instant start = Instant.parse("2026-07-26T10:00:00Z");
        MutableClock clock = new MutableClock(start);
        LoginAttemptLimiter limiter = new LoginAttemptLimiter(clock);

        assertTrue(limiter.tryAcquire("User@Example.com"));
        assertTrue(limiter.tryAcquire("user@example.com"));
        assertTrue(limiter.tryAcquire(" user@example.com "));
        assertFalse(limiter.tryAcquire("USER@example.com"));

        clock.set(start.plus(LoginAttemptLimiter.ATTEMPT_WINDOW));
        assertTrue(limiter.tryAcquire("user@example.com"));
    }

    @Test
    void successfulLoginClearsFailures() {
        LoginAttemptLimiter limiter = new LoginAttemptLimiter(
                Clock.fixed(Instant.parse("2026-07-26T10:00:00Z"), ZoneOffset.UTC));

        limiter.tryAcquire("user@example.com");
        limiter.tryAcquire("user@example.com");
        limiter.tryAcquire("user@example.com");
        limiter.recordSuccess("user@example.com");

        assertTrue(limiter.tryAcquire("user@example.com"));
    }

    @Test
    void permitsOnlyThreeConcurrentAttempts() {
        LoginAttemptLimiter limiter = new LoginAttemptLimiter(
                Clock.fixed(Instant.parse("2026-07-26T10:00:00Z"), ZoneOffset.UTC));

        long permitted = IntStream.range(0, 20)
                .parallel()
                .filter(ignored -> limiter.tryAcquire("user@example.com"))
                .count();

        assertEquals(LoginAttemptLimiter.MAX_ATTEMPTS, permitted);
    }

    private static final class MutableClock extends Clock {

        private Instant instant;

        private MutableClock(Instant instant) {
            this.instant = instant;
        }

        void set(Instant instant) {
            this.instant = instant;
        }

        @Override
        public ZoneOffset getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(java.time.ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }
}
