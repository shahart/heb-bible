package edu.hebbible.auth;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@ConditionalOnProperty(name = "hebbible.auth.rate-limit.storage", havingValue = "postgres")
class PostgresLoginAttemptLimiter implements LoginAttemptLimiter {

    private final JdbcTemplate jdbcTemplate;

    PostgresLoginAttemptLimiter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public boolean tryAcquire(String email) {
        String key = LoginAttemptLimiter.key(email);
        jdbcTemplate.queryForList("""
                SELECT pg_advisory_xact_lock(hashtextextended(?, 0))
                """, key);
        jdbcTemplate.update("""
                DELETE FROM login_attempts
                WHERE attempted_at <= CURRENT_TIMESTAMP - make_interval(secs => ?)
                """, ATTEMPT_WINDOW.toSeconds());

        Long attemptCount = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM login_attempts
                WHERE account_key = ?
                """, Long.class, key);
        if (attemptCount != null && attemptCount >= MAX_ATTEMPTS) {
            return false;
        }

        jdbcTemplate.update("""
                INSERT INTO login_attempts(account_key, attempt_id, attempted_at)
                VALUES (?, ?, CURRENT_TIMESTAMP)
                """, key, UUID.randomUUID());
        return true;
    }

    @Override
    public void recordSuccess(String email) {
        jdbcTemplate.update("""
                DELETE FROM login_attempts
                WHERE account_key = ?
                """, LoginAttemptLimiter.key(email));
    }
}
