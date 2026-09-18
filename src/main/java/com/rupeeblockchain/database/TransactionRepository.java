package com.rupeeblockchain.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.rupeeblockchain.model.Transaction;
import com.rupeeblockchain.model.TransactionStatus;

public class TransactionRepository {

    public TransactionRepository() {
        // Repository class
    }

    public void saveTransaction(
            Transaction transaction) {

        String sql =
                """
                MERGE INTO transactions
                KEY(transaction_id)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection =
                     DatabaseManager.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    transaction.getTransactionId()
            );

            statement.setString(
                    2,
                    transaction.getSenderId()
            );

            statement.setString(
                    3,
                    transaction.getReceiverId()
            );

            statement.setBigDecimal(
                    4,
                    transaction.getAmount()
            );

            statement.setTimestamp(
                    5,
                    Timestamp.valueOf(
                            transaction.getTimestamp()
                    )
            );

            statement.setString(
                    6,
                    transaction.getStatus().name()
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Failed to save transaction.",
                    e
            );
        }
    }

    public void updateTransactionStatus(
            String transactionId,
            TransactionStatus status) {

        String sql =
                """
                UPDATE transactions
                SET status = ?
                WHERE transaction_id = ?
                """;

        try (Connection connection =
                     DatabaseManager.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    status.name()
            );

            statement.setString(
                    2,
                    transactionId
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Failed to update transaction status.",
                    e
            );
        }
    }

    public Transaction findTransactionById(
            String transactionId) {

        String sql =
                """
                SELECT
                    transaction_id,
                    sender_id,
                    receiver_id,
                    amount,
                    transaction_time,
                    status
                FROM transactions
                WHERE transaction_id = ?
                """;

        try (Connection connection =
                     DatabaseManager.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    transactionId
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {

                    Transaction transaction =
                            createTransactionFromResultSet(
                                    resultSet
                            );

                    return transaction;
                }
            }

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Failed to find transaction.",
                    e
            );
        }

        return null;
    }

    public List<Transaction>
    findTransactionsByStatus(
            TransactionStatus status) {

        List<Transaction> transactions =
                new ArrayList<>();

        String sql =
                """
                SELECT
                    transaction_id,
                    sender_id,
                    receiver_id,
                    amount,
                    transaction_time,
                    status
                FROM transactions
                WHERE status = ?
                ORDER BY transaction_time
                """;

        try (Connection connection =
                     DatabaseManager.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    status.name()
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {

                    transactions.add(
                            createTransactionFromResultSet(
                                    resultSet
                            )
                    );
                }
            }

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Failed to load transactions.",
                    e
            );
        }

        return transactions;
    }

    public List<Transaction>
    findTransactionsForUser(
            String userId) {

        List<Transaction> transactions =
                new ArrayList<>();

        String sql =
                """
                SELECT
                    transaction_id,
                    sender_id,
                    receiver_id,
                    amount,
                    transaction_time,
                    status
                FROM transactions
                WHERE sender_id = ?
                   OR receiver_id = ?
                ORDER BY transaction_time
                """;

        try (Connection connection =
                     DatabaseManager.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    userId
            );

            statement.setString(
                    2,
                    userId
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {

                    transactions.add(
                            createTransactionFromResultSet(
                                    resultSet
                            )
                    );
                }
            }

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Failed to load user transactions.",
                    e
            );
        }

        return transactions;
    }

    public List<Transaction>
    findAllTransactions() {

        List<Transaction> transactions =
                new ArrayList<>();

        String sql =
                """
                SELECT
                    transaction_id,
                    sender_id,
                    receiver_id,
                    amount,
                    transaction_time,
                    status
                FROM transactions
                ORDER BY transaction_time
                """;

        try (Connection connection =
                     DatabaseManager.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql);

             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {

                transactions.add(
                        createTransactionFromResultSet(
                                resultSet
                        )
                );
            }

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Failed to load all transactions.",
                    e
            );
        }

        return transactions;
    }

    private Transaction createTransactionFromResultSet(
            ResultSet resultSet)
            throws SQLException {

        Transaction transaction =
                new Transaction(
                        resultSet.getString(
                                "transaction_id"
                        ),
                        resultSet.getString(
                                "sender_id"
                        ),
                        resultSet.getString(
                                "receiver_id"
                        ),
                        resultSet.getBigDecimal(
                                "amount"
                        )
                );

        transaction.setStatus(
                TransactionStatus.valueOf(
                        resultSet.getString(
                                "status"
                        )
                )
        );

        return transaction;
    }
}