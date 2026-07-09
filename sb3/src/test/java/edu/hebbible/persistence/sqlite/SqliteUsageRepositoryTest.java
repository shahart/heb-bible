package edu.hebbible.persistence.sqlite;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SqliteUsageRepositoryTest {

    @TempDir
    Path tempDir;

    @Test
    void recordInvocationReturnsCountPerUserAndEndpoint() {
        SqliteUsageRepository repository =
                new SqliteUsageRepository(tempDir.resolve("usage.db").toString());
        repository.init();

        assertEquals(1, repository.recordInvocation("one@example.com", "psukim"));
        assertEquals(2, repository.recordInvocation("one@example.com", "psukim"));
        assertEquals(1, repository.recordInvocation("one@example.com", "dilugim"));
        assertEquals(1, repository.recordInvocation("two@example.com", "psukim"));
    }

    @Test
    void recordInvocationStoresOneRowWithCountAndLastUsedDate() throws Exception {
        Path db = tempDir.resolve("usage.db");
        SqliteUsageRepository repository = new SqliteUsageRepository(db.toString());
        repository.init();

        repository.recordInvocation("one@example.com", "psukim");
        repository.recordInvocation("one@example.com", "psukim");

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + db);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("""
                     SELECT COUNT(*), invocation_count, last_used_date
                     FROM endpoint_usage
                     WHERE user_id = 'one@example.com'
                       AND endpoint = 'psukim'
                     """)) {
            assertEquals(1, resultSet.getInt(1));
            assertEquals(2, resultSet.getInt(2));
            assertNotNull(resultSet.getString(3));
        }
    }
}
