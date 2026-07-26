package edu.hebbible.auth;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.PriorityBlockingQueue;

@Component
class LoginAttemptLimiter {

    static final int MAX_ATTEMPTS = 3;
    static final Duration ATTEMPT_WINDOW = Duration.ofMinutes(5);

    private final ConcurrentMap<String, AttemptWindow> attemptsByEmail = new ConcurrentHashMap<>();
    private final PriorityBlockingQueue<Expiry> expiries = new PriorityBlockingQueue<>();
    private final Clock clock;

    LoginAttemptLimiter() {
        this(Clock.systemUTC());
    }

    LoginAttemptLimiter(Clock clock) {
        this.clock = clock;
    }

    boolean tryAcquire(String email) {
        Instant now = clock.instant();
        removeExpiredAttempts(now);
        String key = normalize(email);
        boolean[] acquired = new boolean[1];
        attemptsByEmail.compute(key, (ignored, attempts) -> {
            AttemptWindow current = attempts == null ? new AttemptWindow() : attempts;
            acquired[0] = current.tryAcquire(now);
            return current;
        });
        if (!acquired[0]) {
            return false;
        }

        expiries.add(new Expiry(key, now.plus(ATTEMPT_WINDOW)));
        return true;
    }

    void recordSuccess(String email) {
        attemptsByEmail.remove(normalize(email));
    }

    private String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private void removeExpiredAttempts(Instant now) {
        Expiry next;
        while ((next = expiries.peek()) != null && !next.expiresAt().isAfter(now)) {
            if (!expiries.remove(next)) {
                continue;
            }
            attemptsByEmail.computeIfPresent(next.email(),
                    (ignored, attempts) -> attempts.removeExpired(now) ? null : attempts);
        }
    }

    private static final class AttemptWindow {

        private final Deque<Instant> attempts = new ArrayDeque<>();

        synchronized boolean tryAcquire(Instant now) {
            removeExpired(now);
            if (attempts.size() >= MAX_ATTEMPTS) {
                return false;
            }
            attempts.addLast(now);
            return true;
        }

        synchronized boolean removeExpired(Instant now) {
            Instant cutoff = now.minus(ATTEMPT_WINDOW);
            while (!attempts.isEmpty() && !attempts.getFirst().isAfter(cutoff)) {
                attempts.removeFirst();
            }
            return attempts.isEmpty();
        }
    }

    private record Expiry(String email, Instant expiresAt) implements Comparable<Expiry> {

        @Override
        public int compareTo(Expiry other) {
            return expiresAt.compareTo(other.expiresAt);
        }
    }
}
