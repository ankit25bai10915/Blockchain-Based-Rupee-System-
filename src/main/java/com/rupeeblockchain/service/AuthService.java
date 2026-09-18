package com.rupeeblockchain.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.rupeeblockchain.database.DatabaseManager;
import com.rupeeblockchain.database.UserRepository;
import com.rupeeblockchain.database.WalletRepository;
import com.rupeeblockchain.model.Admin;
import com.rupeeblockchain.model.User;
import com.rupeeblockchain.model.Wallet;

public class AuthService {

    private final List<User> users;

    private final Admin admin;

    private final UserRepository userRepository;

    private final WalletRepository walletRepository;

    public AuthService() {

        DatabaseManager.initializeDatabase();

        userRepository = new UserRepository();

        walletRepository = new WalletRepository();

        users = new ArrayList<>();

        loadUsersFromDatabase();

        seedDemoUsersIfRequired();

        loadUsersFromDatabase();

        admin = new Admin(
                "admin",
                "admin123"
        );
    }

    private void loadUsersFromDatabase() {

        users.clear();

        List<User> databaseUsers =
                userRepository.findAllUsers();

        for (User databaseUser : databaseUsers) {

            Wallet wallet =
                    walletRepository.findWalletByOwnerId(
                            databaseUser.getUserId()
                    );

            if (wallet != null) {

                User user =
                        new User(
                                databaseUser.getUserId(),
                                databaseUser.getName(),
                                databaseUser.getEmail(),
                                databaseUser.getPassword(),
                                wallet
                        );

                users.add(user);
            }
        }
    }

    private void seedDemoUsersIfRequired() {

        if (!users.isEmpty()) {
            return;
        }

        Wallet aliceWallet =
                new Wallet(
                        "W1001",
                        "U1001",
                        new BigDecimal("10000.00")
                );

        User alice =
                new User(
                        "U1001",
                        "Alice",
                        "alice@gmail.com",
                        "alice123",
                        aliceWallet
                );

        Wallet bobWallet =
                new Wallet(
                        "W1002",
                        "U1002",
                        new BigDecimal("5000.00")
                );

        User bob =
                new User(
                        "U1002",
                        "Bob",
                        "bob@gmail.com",
                        "bob123",
                        bobWallet
                );

        userRepository.saveUser(alice);

        walletRepository.saveWallet(
                aliceWallet
        );

        userRepository.saveUser(bob);

        walletRepository.saveWallet(
                bobWallet
        );
    }

    public User authenticateUser(
            String userId,
            String password) {

        for (User user : users) {

            if (user.getUserId().equals(userId)
                    && user.getPassword().equals(password)) {

                return user;
            }
        }

        return null;
    }

    public boolean authenticateAdmin(
            String username,
            String password) {

        return admin.getUsername()
                .equals(username)
                && admin.getPassword()
                .equals(password);
    }

    public User findUserById(
            String userId) {

        for (User user : users) {

            if (user.getUserId()
                    .equals(userId)) {

                return user;
            }
        }

        return null;
    }

    public List<User> getAllUsers() {

        return new ArrayList<>(users);
    }

    public void refreshUsersFromDatabase() {

        loadUsersFromDatabase();
    }

    public WalletRepository getWalletRepository() {

        return walletRepository;
    }

    // =========================================================
    // NEW USER REGISTRATION
    // =========================================================

    public boolean registerUser(
            String userId,
            String name,
            String email,
            String password) {

        userId = userId.trim();
        name = name.trim();
        email = email.trim();

        if (userId.isEmpty()
                || name.isEmpty()
                || email.isEmpty()
                || password == null
                || password.isEmpty()) {

            throw new IllegalArgumentException(
                    "All fields are required."
            );
        }

        if (findUserById(userId) != null) {

            throw new IllegalArgumentException(
                    "User ID already exists."
            );
        }

        if (userRepository.findUserById(userId) != null) {

            throw new IllegalArgumentException(
                    "User ID already exists in the database."
            );
        }

        String walletId =
                generateWalletId();

        Wallet wallet =
                new Wallet(
                        walletId,
                        userId,
                        BigDecimal.ZERO
                );

        User user =
                new User(
                        userId,
                        name,
                        email,
                        password,
                        wallet
                );

        userRepository.saveUser(user);

        walletRepository.saveWallet(wallet);

        users.add(user);

        return true;
    }

    private String generateWalletId() {

        int highestNumber = 1000;

        for (User user : users) {

            String walletId =
                    user.getWallet().getWalletId();

            if (walletId == null) {
                continue;
            }

            if (walletId.startsWith("W")) {

                try {

                    int number =
                            Integer.parseInt(
                                    walletId.substring(1)
                            );

                    if (number > highestNumber) {
                        highestNumber = number;
                    }

                } catch (NumberFormatException ignored) {
                    // Ignore wallets with non-numeric IDs.
                }
            }
        }

        return "W" + (highestNumber + 1);
    }
}