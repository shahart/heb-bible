package edu.hebbible.auth;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

@Repository
@ConditionalOnProperty(
        name = "hebbible.auth.user-management.provider",
        havingValue = "jdbc",
        matchIfMissing = true)
public class LocalUserRepository {

    private final JdbcTemplate jdbcTemplate;

    public LocalUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<LocalUser> findByEmail(String email) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("""
                            SELECT id, email, password_hash
                            FROM local_users
                            WHERE email = ?
                            """,
                    (rs, rowNum) -> new LocalUser(
                            rs.getLong("id"),
                            rs.getString("email"),
                            rs.getString("password_hash")),
                    normalize(email)));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Transactional
    public LocalUser create(String email, String passwordHash) {
        String normalizedEmail = normalize(email);
        jdbcTemplate.update("""
                        INSERT INTO local_users(email, password_hash, created_at)
                        VALUES (?, ?, CURRENT_TIMESTAMP)
                        """,
                normalizedEmail, passwordHash);
        return findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalStateException("Local user was not created"));
    }

    private String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
