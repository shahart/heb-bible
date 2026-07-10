package edu.hebbible.persistence.postgres;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PostgresUsageRepositoryTest {

    @Test
    void recordInvocationUsesPostgresUpsert() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        when(jdbcTemplate.queryForObject(startsWith("SELECT invocation_count"), eq(Integer.class),
                eq("one@example.com"), eq("psukim"))).thenReturn(3);
        PostgresUsageRepository repository = new PostgresUsageRepository(jdbcTemplate);

        assertEquals(3, repository.recordInvocation("one@example.com", "psukim"));

        verify(jdbcTemplate).update(startsWith("INSERT INTO endpoint_usage"), eq("one@example.com"), eq("psukim"));
        assertTrue(repository.upsertSql().contains("ON CONFLICT (user_id, endpoint) DO UPDATE SET"));
        assertTrue(repository.upsertSql().contains("endpoint_usage.invocation_count + 1"));
    }
}
