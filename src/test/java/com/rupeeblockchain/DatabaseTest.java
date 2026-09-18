package com.rupeeblockchain;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.rupeeblockchain.database.DatabaseManager;

public class DatabaseTest {

    @Test
    public void databaseConnectionTest()
            throws Exception {

        DatabaseManager.initializeDatabase();

        try (Connection connection =
                     DatabaseManager.getConnection()) {

            assertNotNull(
                    connection
            );

            assertTrue(
                    connection.isValid(2)
            );
        }
    }

    @Test
    public void requiredTablesShouldExist()
            throws Exception {

        DatabaseManager.initializeDatabase();

        try (Connection connection =
                     DatabaseManager.getConnection();
             Statement statement =
                     connection.createStatement()) {

            String sql =
                    """
                    SELECT COUNT(*)
                    FROM INFORMATION_SCHEMA.TABLES
                    WHERE TABLE_SCHEMA = 'PUBLIC'
                    AND TABLE_NAME IN (
                        'USERS',
                        'WALLETS',
                        'TRANSACTIONS',
                        'BLOCKS',
                        'BLOCK_TRANSACTIONS'
                    )
                    """;

            try (ResultSet resultSet =
                         statement.executeQuery(sql)) {

                assertTrue(
                        resultSet.next()
                );

                int tableCount =
                        resultSet.getInt(1);

                assertTrue(
                        tableCount >= 5,
                        "All required database tables should exist."
                );
            }
        }
    }
}