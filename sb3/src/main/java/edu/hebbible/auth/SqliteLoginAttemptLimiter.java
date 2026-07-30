package edu.hebbible.auth;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// TODO

@Component
@ConditionalOnProperty(name = "hebbible.auth.rate-limit.storage", havingValue = "sqlite")
class SqliteLoginAttemptLimiter implements LoginAttemptLimiter {

//    private final JdbcTemplate jdbcTemplate;

    SqliteLoginAttemptLimiter(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public boolean tryAcquire(String email) {
        return true;
    }

    @Override
    public void recordSuccess(String email) {
    }
}
