package com.rupeeblockchain.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.rupeeblockchain.model.User;

public class UserRepository {

    public UserRepository() {
        // Repository class
    }

    public void saveUser(User user) {

        String sql =
                """
                MERGE INTO users
                KEY(user_id)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection =
                     DatabaseManager.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    user.getUserId()
            );

            statement.setString(
                    2,
                    user.getName()
            );

            statement.setString(
                    3,
                    user.getEmail()
            );

            statement.setString(
                    4,
                    user.getPassword()
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Failed to save user.",
                    e
            );
        }
    }

    public User findUserById(String userId) {

        String sql =
                """
                SELECT
                    user_id,
                    name,
                    email,
                    password
                FROM users
                WHERE user_id = ?
                """;

        try (Connection connection =
                     DatabaseManager.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    userId
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {

                    return new User(
                            resultSet.getString("user_id"),
                            resultSet.getString("name"),
                            resultSet.getString("email"),
                            resultSet.getString("password"),
                            null
                    );
                }
            }

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Failed to find user.",
                    e
            );
        }

        return null;
    }

    public List<User> findAllUsers() {

        List<User> users =
                new ArrayList<>();

        String sql =
                """
                SELECT
                    user_id,
                    name,
                    email,
                    password
                FROM users
                ORDER BY user_id
                """;

        try (Connection connection =
                     DatabaseManager.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql);

             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {

                users.add(
                        new User(
                                resultSet.getString("user_id"),
                                resultSet.getString("name"),
                                resultSet.getString("email"),
                                resultSet.getString("password"),
                                null
                        )
                );
            }

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Failed to load users.",
                    e
            );
        }

        return users;
    }
}