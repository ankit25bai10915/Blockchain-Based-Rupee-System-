package com.rupeeblockchain.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.rupeeblockchain.model.Block;
import com.rupeeblockchain.model.Transaction;
import com.rupeeblockchain.model.TransactionStatus;

public class BlockRepository {

    private final TransactionRepository
            transactionRepository;

    public BlockRepository() {

        transactionRepository =
                new TransactionRepository();
    }

    public void saveBlock(Block block) {

        String blockSql =
                """
                MERGE INTO blocks
                KEY(block_index)
                VALUES (?, ?, ?, ?, ?)
                """;

        String transactionSql =
                """
                MERGE INTO block_transactions
                KEY(block_index, transaction_id)
                VALUES (?, ?)
                """;

        try (Connection connection =
                     DatabaseManager.getConnection()) {

            connection.setAutoCommit(false);

            try (PreparedStatement blockStatement =
                         connection.prepareStatement(
                                 blockSql
                         );

                 PreparedStatement transactionStatement =
                         connection.prepareStatement(
                                 transactionSql
                         )) {

                blockStatement.setInt(
                        1,
                        block.getIndex()
                );

                blockStatement.setTimestamp(
                        2,
                        Timestamp.valueOf(
                                block.getTimestamp()
                        )
                );

                blockStatement.setString(
                        3,
                        block.getPreviousHash()
                );

                blockStatement.setString(
                        4,
                        block.getHash()
                );

                blockStatement.setLong(
                        5,
                        block.getNonce()
                );

                blockStatement.executeUpdate();

                for (Transaction transaction :
                        block.getTransactions()) {

                    transactionRepository
                            .saveTransaction(
                                    transaction
                            );

                    transactionStatement.setInt(
                            1,
                            block.getIndex()
                    );

                    transactionStatement.setString(
                            2,
                            transaction
                                    .getTransactionId()
                    );

                    transactionStatement
                            .executeUpdate();
                }

                connection.commit();

            } catch (SQLException e) {

                connection.rollback();

                throw e;
            }

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Failed to save block.",
                    e
            );
        }
    }

    public List<Block>
    findAllBlocks() {

        List<Block> blocks =
                new ArrayList<>();

        String sql =
                """
                SELECT
                    block_index,
                    block_timestamp,
                    previous_hash,
                    hash,
                    nonce
                FROM blocks
                ORDER BY block_index
                """;

        try (Connection connection =
                     DatabaseManager.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql);

             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {

                int index =
                        resultSet.getInt(
                                "block_index"
                        );

                LocalDateTime timestamp =
                        resultSet
                                .getTimestamp(
                                        "block_timestamp"
                                )
                                .toLocalDateTime();

                String previousHash =
                        resultSet.getString(
                                "previous_hash"
                        );

                String hash =
                        resultSet.getString(
                                "hash"
                        );

                long nonce =
                        resultSet.getLong(
                                "nonce"
                        );

                List<Transaction>
                        transactions =
                        findBlockTransactions(
                                index
                        );

                Block block =
                        new Block(
                                index,
                                timestamp,
                                transactions,
                                previousHash,
                                hash,
                                nonce
                        );

                blocks.add(block);
            }

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Failed to load blocks.",
                    e
            );
        }

        return blocks;
    }

    private List<Transaction>
    findBlockTransactions(
            int blockIndex) {

        List<Transaction>
                transactions =
                new ArrayList<>();

        String sql =
                """
                SELECT
                    t.transaction_id,
                    t.sender_id,
                    t.receiver_id,
                    t.amount,
                    t.transaction_time,
                    t.status
                FROM transactions t
                INNER JOIN block_transactions bt
                    ON t.transaction_id =
                       bt.transaction_id
                WHERE bt.block_index = ?
                ORDER BY t.transaction_time
                """;

        try (Connection connection =
                     DatabaseManager.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    blockIndex
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {

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
                                    ),
                                    resultSet.getTimestamp(
                                            "transaction_time"
                                    ).toLocalDateTime(),
                                    TransactionStatus.valueOf(
                                            resultSet.getString(
                                                    "status"
                                            )
                                    )
                            );

                    transactions.add(
                            transaction
                    );
                }
            }

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Failed to load block transactions.",
                    e
            );
        }

        return transactions;
    }
}