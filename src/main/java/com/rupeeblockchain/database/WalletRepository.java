package com.rupeeblockchain.database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.rupeeblockchain.model.Wallet;

public class WalletRepository {

    public WalletRepository() {
        // Repository class
    }

    public void saveWallet(Wallet wallet) {

        String sql =
                """
                MERGE INTO wallets
                KEY(wallet_id)
                VALUES (?, ?, ?)
                """;

        try (Connection connection =
                     DatabaseManager.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    wallet.getWalletId()
            );

            statement.setString(
                    2,
                    wallet.getOwnerId()
            );

            statement.setBigDecimal(
                    3,
                    wallet.getBalance()
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Failed to save wallet.",
                    e
            );
        }
    }

    public Wallet findWalletByOwnerId(
            String ownerId) {

        String sql =
                """
                SELECT
                    wallet_id,
                    owner_id,
                    balance
                FROM wallets
                WHERE owner_id = ?
                """;

        try (Connection connection =
                     DatabaseManager.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    ownerId
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {

                    return new Wallet(
                            resultSet.getString("wallet_id"),
                            resultSet.getString("owner_id"),
                            resultSet.getBigDecimal("balance")
                    );
                }
            }

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Failed to find wallet.",
                    e
            );
        }

        return null;
    }

    public List<Wallet> findAllWallets() {

        List<Wallet> wallets =
                new ArrayList<>();

        String sql =
                """
                SELECT
                    wallet_id,
                    owner_id,
                    balance
                FROM wallets
                ORDER BY wallet_id
                """;

        try (Connection connection =
                     DatabaseManager.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql);

             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {

                wallets.add(
                        new Wallet(
                                resultSet.getString("wallet_id"),
                                resultSet.getString("owner_id"),
                                resultSet.getBigDecimal("balance")
                        )
                );
            }

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Failed to load wallets.",
                    e
            );
        }

        return wallets;
    }

    public void updateBalance(
            String ownerId,
            BigDecimal newBalance) {

        String sql =
                """
                UPDATE wallets
                SET balance = ?
                WHERE owner_id = ?
                """;

        try (Connection connection =
                     DatabaseManager.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setBigDecimal(
                    1,
                    newBalance
            );

            statement.setString(
                    2,
                    ownerId
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Failed to update wallet balance.",
                    e
            );
        }
    }
}
