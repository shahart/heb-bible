package edu.hebbible.persistence.jdbc;

import edu.hebbible.persistence.UsageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

public abstract class JdbcUsageRepository implements UsageRepository {

    private static final Logger log = LoggerFactory.getLogger(JdbcUsageRepository.class);

    private final JdbcTemplate jdbcTemplate;

    protected JdbcUsageRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public int recordInvocation(String userId, String endpoint) {
        try {
            jdbcTemplate.update(upsertSql(), userId, endpoint);
            Integer count = jdbcTemplate.queryForObject("""
                    SELECT invocation_count
                    FROM endpoint_usage
                    WHERE user_id = ?
                      AND endpoint = ?
                    """, Integer.class, userId, endpoint);
            return count == null ? 0 : count;
        } catch (RuntimeException e) {
            log.error("recordInvocation failed", e);
            throw new IllegalStateException("Failed to record endpoint usage", e);
        }
    }

    protected abstract String upsertSql();
}
