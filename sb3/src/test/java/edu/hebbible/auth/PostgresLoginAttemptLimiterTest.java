package edu.hebbible.auth;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PostgresLoginAttemptLimiterTest {

    @Test
    void permitsAndStoresAttemptWhenBelowLimit() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        PostgresLoginAttemptLimiter limiter = new PostgresLoginAttemptLimiter(jdbcTemplate);
        when(jdbcTemplate.queryForList(startsWith("SELECT pg_advisory_xact_lock"), any(String.class)))
                .thenReturn(List.of());
        when(jdbcTemplate.queryForObject(startsWith("SELECT COUNT(*)"), eq(Long.class), any(String.class)))
                .thenReturn(2L);

        assertTrue(limiter.tryAcquire("user@example.com"));

        verify(jdbcTemplate).update(
                startsWith("INSERT INTO login_attempts"), any(String.class), any(UUID.class));
    }

    @Test
    void rejectsWithoutInsertingWhenLimitIsReached() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        PostgresLoginAttemptLimiter limiter = new PostgresLoginAttemptLimiter(jdbcTemplate);
        when(jdbcTemplate.queryForList(startsWith("SELECT pg_advisory_xact_lock"), any(String.class)))
                .thenReturn(List.of());
        when(jdbcTemplate.queryForObject(startsWith("SELECT COUNT(*)"), eq(Long.class), any(String.class)))
                .thenReturn(3L);

        assertFalse(limiter.tryAcquire("user@example.com"));

        verify(jdbcTemplate, never()).update(
                startsWith("INSERT INTO login_attempts"), any(String.class), any(UUID.class));
    }

    @Test
    void successfulLoginDeletesAttempts() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        PostgresLoginAttemptLimiter limiter = new PostgresLoginAttemptLimiter(jdbcTemplate);

        limiter.recordSuccess(" User@Example.com ");

        verify(jdbcTemplate).update(
                startsWith("DELETE FROM login_attempts"),
                eq("hebbible:auth:login-attempts:b4c9a289323b21a01c3e940f150eb9b8c542587f1abfd8f0e1cc1ffc5e475514"));
    }
}
