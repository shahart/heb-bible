package edu.hebbible.persistence.postgres;

import edu.hebbible.controller.Controller;
import edu.hebbible.persistence.jdbc.JdbcUsageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "hebbible.usage.database", havingValue = "postgres")
public class PostgresUsageRepository extends JdbcUsageRepository {

    private static final Logger log = LoggerFactory.getLogger(PostgresUsageRepository.class);

    public PostgresUsageRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
        log.info("PostgresUsageRepository initialized");
    }

    @Override
    protected String upsertSql() {
        return """
                INSERT INTO endpoint_usage(user_id, endpoint, invocation_count, last_used_date)
                VALUES (?, ?, 1, CURRENT_TIMESTAMP)
                ON CONFLICT (user_id, endpoint) DO UPDATE SET
                    invocation_count = endpoint_usage.invocation_count + 1,
                    last_used_date = CURRENT_TIMESTAMP
                """;
    }
}
