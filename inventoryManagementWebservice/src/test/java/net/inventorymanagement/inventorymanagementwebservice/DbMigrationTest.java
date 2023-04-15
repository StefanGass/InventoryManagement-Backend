package net.inventorymanagement.inventorymanagementwebservice;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.springframework.test.context.ActiveProfiles;

/**
 * Liquibase Migration End2End Test
 * Uses MariaDB running @ localhost, create new temporary DB "test_inventorymanagement" and runs
 * migration script inside. Afterward, "test_inventorymanagement" will be deleted again.
 */
@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public class DbMigrationTest {
    private String dbUsername = "inventorymanagement";
    private String dbPassword = "YOUR_SUPER_SECRET_PASSWORD";
    private String dbName = "test_inventorymanagement";
    private String dbUrl = "jdbc:mysql://127.0.0.1:3306/?allowMultiQueries=true";
    private String dbUrlWithSchema = "jdbc:mysql://127.0.0.1:3306/" + dbName + "?allowMultiQueries=true";

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        log.info("Connecting to DB {} with user {}", dbUrl, dbUsername);
        connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        Statement createTestDbStatement = connection.createStatement();
        String sql = "CREATE DATABASE " + dbName;
        log.info("Creating test database for migration {}", dbName);
        createTestDbStatement.executeUpdate(sql);
        log.info("Successfully created test database {}", dbUrlWithSchema);
        connection = DriverManager.getConnection(dbUrlWithSchema, dbUsername, dbPassword);
    }

    @AfterEach
    void tearDown() throws SQLException {
        Statement dropTestDbStatement = connection.createStatement();
        log.info("Dropping test database after migration {}", dbName);
        String sql = "DROP DATABASE " + dbName;
        dropTestDbStatement.executeUpdate(sql);
        log.info("Successfully dropped test database {} after migration", dbName);
    }

    @Test
    void shouldMigrateAllChangelogs() throws LiquibaseException {
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        database.setDefaultSchemaName(dbName);
        Liquibase liquibase = new liquibase.Liquibase("config/liquibase/master.xml", new ClassLoaderResourceAccessor(), database);
        log.info("Running liquibase migration scripts on {}", dbUrlWithSchema);
        liquibase.update(new Contexts(), new LabelExpression());
        log.info("Successfully ran liquibase migration scripts on {}", dbUrlWithSchema);
    }
}
