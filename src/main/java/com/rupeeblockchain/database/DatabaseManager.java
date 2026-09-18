package com.rupeeblockchain.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DATABASE_URL =
            "jdbc:h2:./database/rupee_blockchain";

    private static final String DATABASE_USER =
            "sa";

    private static final String DATABASE_PASSWORD =
            "";

    private DatabaseManager() {
        // Utility class
    }

    public static Connection getConnection()
            throws SQLException {

        return DriverManager.getConnection(
                DATABASE_URL,
                DATABASE_USER,
                DATABASE_PASSWORD
        );
    }

    public static void initializeDatabase() {

        String createUsersTable =
                """
                CREATE TABLE IF NOT EXISTS users (
                    user_id VARCHAR(20) PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    email VARCHAR(150) NOT NULL,
                    password VARCHAR(255) NOT NULL
                )
                """;

        String createWalletsTable =
                """
                CREATE TABLE IF NOT EXISTS wallets (
                    wallet_id VARCHAR(20) PRIMARY KEY,
                    owner_id VARCHAR(20) NOT NULL,
                    balance DECIMAL(15,2) NOT NULL,
                    FOREIGN KEY (owner_id)
                    REFERENCES users(user_id)
                )
                """;

        String createTransactionsTable =
                """
                CREATE TABLE IF NOT EXISTS transactions (
                    transaction_id VARCHAR(30) PRIMARY KEY,
                    sender_id VARCHAR(20) NOT NULL,
                    receiver_id VARCHAR(20) NOT NULL,
                    amount DECIMAL(15,2) NOT NULL,
                    transaction_time TIMESTAMP NOT NULL,
                    status VARCHAR(20) NOT NULL
                )
                """;

        String createBlocksTable =
                """
                CREATE TABLE IF NOT EXISTS blocks (
                    block_index INT PRIMARY KEY,
                    block_timestamp TIMESTAMP NOT NULL,
                    previous_hash VARCHAR(64) NOT NULL,
                    hash VARCHAR(64) NOT NULL,
                    nonce BIGINT NOT NULL
                )
                """;

        String createBlockTransactionsTable =
                """
                CREATE TABLE IF NOT EXISTS block_transactions (
                    block_index INT NOT NULL,
                    transaction_id VARCHAR(30) NOT NULL,
                    PRIMARY KEY (block_index, transaction_id),
                    FOREIGN KEY (block_index)
                    REFERENCES blocks(block_index),
                    FOREIGN KEY (transaction_id)
                    REFERENCES transactions(transaction_id)
                )
                """;

        try (Connection connection =
                     getConnection();

             Statement statement =
                     connection.createStatement()) {

            statement.executeUpdate(
                    createUsersTable
            );

            statement.executeUpdate(
                    createWalletsTable
            );

            statement.executeUpdate(
                    createTransactionsTable
            );

            statement.executeUpdate(
                    createBlocksTable
            );

            statement.executeUpdate(
                    createBlockTransactionsTable
            );

            System.out.println(
                    "Database initialized successfully."
            );

        } catch (SQLException e) {

            System.out.println(
                    "Database initialization failed."
            );

            e.printStackTrace();
        }
    }

    /*
     * TEMPORARY METHOD
     *
     * Deletes only blockchain records.
     *
     * Users, wallets and transactions are NOT deleted.
     */
    public static void resetBlockchain() {

        String deleteBlockTransactions =
                "DELETE FROM block_transactions";

        String deleteBlocks =
                "DELETE FROM blocks";

        try (Connection connection =
                     getConnection();

             Statement statement =
                     connection.createStatement()) {

            connection.setAutoCommit(false);

            try {

                statement.executeUpdate(
                        deleteBlockTransactions
                );

                statement.executeUpdate(
                        deleteBlocks
                );

                connection.commit();

                System.out.println(
                        "Blockchain data reset successfully."
                );

                System.out.println(
                        "Users, wallets and transactions were preserved."
                );

            } catch (SQLException e) {

                connection.rollback();

                throw e;
            }

        } catch (SQLException e) {

            System.out.println(
                    "Failed to reset blockchain data."
            );

            e.printStackTrace();
        }
    }
}