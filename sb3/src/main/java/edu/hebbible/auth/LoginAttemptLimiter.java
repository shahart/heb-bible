package edu.hebbible.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
import java.util.Locale;

interface LoginAttemptLimiter {

    int MAX_ATTEMPTS = 3;
    Duration ATTEMPT_WINDOW = Duration.ofMinutes(5);
    String KEY_PREFIX = "hebbible:auth:login-attempts:";

    boolean tryAcquire(String email);

    void recordSuccess(String email);

    static String key(String email) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(normalizedEmail.getBytes(StandardCharsets.UTF_8));
            return KEY_PREFIX + HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
