package edu.hebbible.persistence.sqlite;

import edu.hebbible.persistence.UsageRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Repository
public class SqliteUsageRepository implements UsageRepository {

    private static final Logger log = LoggerFactory.getLogger(SqliteUsageRepository.class);

    private final String jdbcUrl;

    public SqliteUsageRepository(
            @Value("${hebbible.usage.sqlite.path:usage.db}") String sqlitePath) {
        log.info("Init'ed, with " + sqlitePath);
        this.jdbcUrl = "jdbc:sqlite:" + sqlitePath;
    }

    @PostConstruct
    void init() {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS endpoint_usage (
                        user_id TEXT NOT NULL,
                        endpoint TEXT NOT NULL,
                        invocation_count INTEGER NOT NULL,
                        last_used_date TEXT NOT NULL,
                        PRIMARY KEY (user_id, endpoint)
                    )
                    """);
        } catch (SQLException e) {
            log.error("init failed", e);
            throw new IllegalStateException("Failed to initialize usage persistence", e);
        }
    }

    @Override
    public int recordInvocation(String userId, String endpoint) {
        try (Connection connection = connect()) {
            connection.setAutoCommit(false);
            try {
                insertInvocation(connection, userId, endpoint);
                int count = countInvocations(connection, userId, endpoint);
                connection.commit();
                return count;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            log.error("recordInvocation failed", e);
            throw new IllegalStateException("Failed to record endpoint usage", e);
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(jdbcUrl);
    }

    private void insertInvocation(Connection connection, String userId, String endpoint) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                INSERT INTO endpoint_usage(user_id, endpoint, invocation_count, last_used_date)
                VALUES (?, ?, 1, CURRENT_TIMESTAMP)
                ON CONFLICT(user_id, endpoint) DO UPDATE SET
                    invocation_count = invocation_count + 1,
                    last_used_date = CURRENT_TIMESTAMP
                """)) {
            statement.setString(1, userId);
            statement.setString(2, endpoint);
            statement.executeUpdate();
        }
    }

    private int countInvocations(Connection connection, String userId, String endpoint) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT invocation_count
                FROM endpoint_usage
                WHERE user_id = ?
                  AND endpoint = ?
                """)) {
            statement.setString(1, userId);
            statement.setString(2, endpoint);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt(1) : 0;
            }
        }
    }
}
