package edu.hebbible.auth;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        name = "hebbible.auth.user-management.provider",
        havingValue = "jdbc",
        matchIfMissing = true)
public class LocalUserSchemaInitializer {

    private final JdbcTemplate jdbcTemplate;
    private final String database;

    public LocalUserSchemaInitializer(JdbcTemplate jdbcTemplate,
                                      @Value("${hebbible.usage.database:sqlite}") String database) {
        this.jdbcTemplate = jdbcTemplate;
        this.database = database;
    }

    @PostConstruct
    void createTableIfMissing() {
        if ("postgres".equalsIgnoreCase(database)) {
            jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS local_users (
                        id BIGSERIAL PRIMARY KEY,
                        email TEXT NOT NULL UNIQUE,
                        password_hash TEXT NOT NULL,
                        created_at TIMESTAMPTZ NOT NULL
                    )
                    """);
        } else {
            jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS local_users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        email TEXT NOT NULL UNIQUE,
                        password_hash TEXT NOT NULL,
                        created_at TEXT NOT NULL
                    )
                    """);
        }
    }
}
