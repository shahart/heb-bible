package edu.hebbible.persistence.sqlite;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SqliteLiquibaseMigrationTest {

    @TempDir
    Path tempDir;

    @Test
    void liquibaseCreatesEndpointUsageTable() throws Exception {
        Path db = tempDir.resolve("usage.db");

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + db)) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase(
                    "db/changelog/sqlite/db.changelog-master.xml",
                    new ClassLoaderResourceAccessor(),
                    database);
            liquibase.update(new Contexts(), new LabelExpression());
        }

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + db);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("""
                     SELECT COUNT(*)
                     FROM sqlite_master
                     WHERE type = 'table'
                       AND name = 'endpoint_usage'
                     """)) {
            assertEquals(1, resultSet.getInt(1));
        }
    }
}
