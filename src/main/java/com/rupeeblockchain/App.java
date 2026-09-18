package com.rupeeblockchain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

import com.rupeeblockchain.model.Transaction;
import com.rupeeblockchain.model.User;
import com.rupeeblockchain.service.AuthService;
import com.rupeeblockchain.service.BlockchainService;
import com.rupeeblockchain.service.TransactionService;

public class App {

    private static final Scanner scanner =
            new Scanner(System.in);

    private static AuthService authService;
    private static TransactionService transactionService;
    private static BlockchainService blockchainService;

    public static void main(String[] args) {

        authService =
                new AuthService();

        transactionService =
                new TransactionService(
                        authService
                );

        blockchainService =
                new BlockchainService(
                        authService,
                        transactionService
                );

        showWelcomeMessage();

        mainMenu();
    }

    private static void showWelcomeMessage() {

        System.out.println();
        System.out.println(
                "=========================================="
        );

        System.out.println(
                "   BLOCKCHAIN-BASED SECURE RUPEE SYSTEM"
        );

        System.out.println(
                "=========================================="
        );

        System.out.println(
                "Digital Rupee Transaction Ledger"
        );

        System.out.println(
                "Simulation only - No real money involved"
        );

        System.out.println(
                "=========================================="
        );
    }

    private static void mainMenu() {

        while (true) {

            System.out.println();
            System.out.println(
                    "============== MAIN MENU =============="
            );

            System.out.println(
                    "1. User Login"
            );

            System.out.println(
                    "2. New User Registration"
            );

            System.out.println(
                    "3. Admin Login"
            );

            System.out.println(
                    "4. Exit"
            );

            System.out.print(
                    "Enter choice: "
            );

            String choice =
                    scanner.nextLine();

            switch (choice) {

                case "1":
                    userLogin();
                    break;

                case "2":
                    registerUser();
                    break;

                case "3":
                    adminLogin();
                    break;

                case "4":

                    System.out.println();
                    System.out.println(
                            "Thank you for using the system."
                    );

                    return;

                default:

                    System.out.println(
                            "Invalid choice."
                    );
            }
        }
    }

    private static void registerUser() {

        System.out.println();
        System.out.println(
                "========== NEW USER REGISTRATION =========="
        );

        System.out.print(
                "User ID: "
        );

        String userId =
                scanner.nextLine().trim();

        System.out.print(
                "Name: "
        );

        String name =
                scanner.nextLine().trim();

        System.out.print(
                "Email: "
        );

        String email =
                scanner.nextLine().trim();

        System.out.print(
                "Password: "
        );

        String password =
                scanner.nextLine();

        try {

            boolean registered =
                    authService.registerUser(
                            userId,
                            name,
                            email,
                            password
                    );

            if (registered) {

                System.out.println();
                System.out.println(
                        "Registration successful!"
                );

                System.out.println(
                        "User ID : " + userId
                );

                User registeredUser =
                        authService.findUserById(userId);

                if (registeredUser != null) {

                    System.out.println(
                            "Wallet ID: "
                                    + registeredUser
                                            .getWallet()
                                            .getWalletId()
                    );
                }

                System.out.println(
                        "Initial Balance: ₹0.00"
                );

                System.out.println(
                        "You can now log in using your User ID and password."
                );
            }

        } catch (IllegalArgumentException e) {

            System.out.println();
            System.out.println(
                    "Registration failed: "
                            + e.getMessage()
            );
        }
    }

    private static void userLogin() {

        System.out.println();
        System.out.println(
                "============== USER LOGIN =============="
        );

        System.out.print(
                "User ID: "
        );

        String userId =
                scanner.nextLine();

        System.out.print(
                "Password: "
        );

        String password =
                scanner.nextLine();

        User user =
                authService.authenticateUser(
                        userId,
                        password
                );

        if (user == null) {

            System.out.println();
            System.out.println(
                    "Invalid user ID or password."
            );

            return;
        }

        System.out.println();
        System.out.println(
                "Login successful."
        );

        System.out.println(
                "Welcome, "
                        + user.getName()
                        + "!"
        );

        userMenu(user);
    }

    private static void userMenu(User user) {

        while (true) {

            System.out.println();
            System.out.println(
                    "============== USER MENU =============="
            );

            System.out.println(
                    "1. View Profile"
            );

            System.out.println(
                    "2. View Wallet"
            );

            System.out.println(
                    "3. Transfer Rupees"
            );

            System.out.println(
                    "4. View Pending Transactions"
            );

            System.out.println(
                    "5. View Transaction History"
            );

            System.out.println(
                    "6. Logout"
            );

            System.out.print(
                    "Enter choice: "
            );

            String choice =
                    scanner.nextLine();

            switch (choice) {

                case "1":
                    showProfile(user);
                    break;

                case "2":
                    showWallet(user);
                    break;

                case "3":
                    transferMoney(user);
                    break;

                case "4":
                    showPendingTransactions(user);
                    break;

                case "5":
                    showTransactionHistory(user);
                    break;

                case "6":

                    System.out.println(
                            "Logged out successfully."
                    );

                    return;

                default:

                    System.out.println(
                            "Invalid choice."
                    );
            }
        }
    }

    private static void showProfile(User user) {

        System.out.println();
        System.out.println(
                "============== PROFILE =============="
        );

        System.out.println(
                "User ID : "
                        + user.getUserId()
        );

        System.out.println(
                "Name    : "
                        + user.getName()
        );

        System.out.println(
                "Email   : "
                        + user.getEmail()
        );

        System.out.println(
                "Wallet  : "
                        + user.getWallet()
                        .getWalletId()
        );
    }

    private static void showWallet(User user) {

        System.out.println();
        System.out.println(
                "============== WALLET =============="
        );

        System.out.println(
                "Wallet ID : "
                        + user.getWallet()
                        .getWalletId()
        );

        System.out.println(
                "Balance   : ₹"
                        + user.getBalance()
        );

        System.out.println(
                "Available : ₹"
                        + transactionService
                        .getAvailableBalance(
                                user.getUserId()
                        )
        );
    }

    private static void transferMoney(User user) {

        System.out.println();
        System.out.println(
                "========== TRANSFER RUPEES =========="
        );

        System.out.print(
                "Receiver User ID: "
        );

        String receiverId =
                scanner.nextLine();

        System.out.print(
                "Amount: ₹"
        );

        String amountInput =
                scanner.nextLine();

        try {

            BigDecimal amount =
                    new BigDecimal(
                            amountInput
                    );

            Transaction transaction =
                    transactionService
                            .createTransaction(
                                    user.getUserId(),
                                    receiverId,
                                    amount
                            );

            System.out.println();
            System.out.println(
                    "Transaction created successfully."
            );

            System.out.println(
                    "Transaction ID: "
                            + transaction
                            .getTransactionId()
            );

            System.out.println(
                    "Status: "
                            + transaction.getStatus()
            );

            System.out.println(
                    "The transaction is waiting "
                            + "for admin mining."
            );

        } catch (NumberFormatException e) {

            System.out.println(
                    "Invalid amount."
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "Transaction failed: "
                            + e.getMessage()
            );
        }
    }

    private static void showPendingTransactions(
            User user) {

        System.out.println();
        System.out.println(
                "========== PENDING TRANSACTIONS =========="
        );

        List<Transaction> transactions =
                transactionService
                        .getPendingTransactionsForUser(
                                user.getUserId()
                        );

        if (transactions.isEmpty()) {

            System.out.println(
                    "No pending transactions."
            );

            return;
        }

        for (Transaction transaction :
                transactions) {

            System.out.println();
            System.out.println(
                    transaction
            );

            System.out.println(
                    "------------------------------------------"
            );
        }
    }

    private static void showTransactionHistory(
            User user) {

        System.out.println();
        System.out.println(
                "========== TRANSACTION HISTORY =========="
        );

        List<Transaction> transactions =
                transactionService
                        .getTransactionHistory(
                                user.getUserId()
                        );

        if (transactions.isEmpty()) {

            System.out.println(
                    "No transaction history."
            );

            return;
        }

        for (Transaction transaction :
                transactions) {

            System.out.println();
            System.out.println(
                    transaction
            );

            System.out.println(
                    "------------------------------------------"
            );
        }
    }

    private static void adminLogin() {

        System.out.println();
        System.out.println(
                "============== ADMIN LOGIN =============="
        );

        System.out.print(
                "Username: "
        );

        String username =
                scanner.nextLine();

        System.out.print(
                "Password: "
        );

        String password =
                scanner.nextLine();

        if (!authService.authenticateAdmin(
                username,
                password
        )) {

            System.out.println();
            System.out.println(
                    "Invalid admin credentials."
            );

            return;
        }

        System.out.println();
        System.out.println(
                "Admin login successful."
        );

        adminMenu();
    }

    private static void adminMenu() {

        while (true) {

            System.out.println();
            System.out.println(
                    "============== ADMIN MENU =============="
            );

            System.out.println(
                    "1. View Pending Transactions"
            );

            System.out.println(
                    "2. Mine Pending Transactions"
            );

            System.out.println(
                    "3. View Blockchain"
            );

            System.out.println(
                    "4. Validate Blockchain"
            );

            System.out.println(
                    "5. Simulate Tampering"
            );

            System.out.println(
                    "6. Restore Blockchain"
            );

            System.out.println(
                    "7. Export Blockchain"
            );

            System.out.println(
                    "8. Create Blockchain Backup"
            );

            System.out.println(
                    "9. View Transaction Log"
            );

            System.out.println(
                    "10. Logout"
            );

            System.out.print(
                    "Enter choice: "
            );

            String choice =
                    scanner.nextLine();

            switch (choice) {

                case "1":
                    adminViewPending();
                    break;

                case "2":
                    blockchainService
                            .minePendingTransactions();
                    break;

                case "3":
    blockchainService
            .displayBlockchain();
    break;

case "4":
    blockchainService
            .validateBlockchain();
    break;

case "5":
    simulateTampering();
    break;

case "6":
    restoreBlockchain();
    break;

case "7":
    exportBlockchain();
    break;

case "8":
    createBlockchainBackup();
    break;

case "9":
    viewTransactionLog();
    break;

case "10":
    System.out.println(
            "Admin logged out."
    );
    return;

                default:

                    System.out.println(
                            "Invalid choice."
                    );
            }
        }
    }

    private static void adminViewPending() {

        System.out.println();
        System.out.println(
                "========== PENDING TRANSACTIONS =========="
        );

        List<Transaction> transactions =
                transactionService
                        .getAllPendingTransactions();

        if (transactions.isEmpty()) {

            System.out.println(
                    "No pending transactions."
            );

            return;
        }

        for (Transaction transaction :
                transactions) {

            System.out.println();
            System.out.println(
                    transaction
            );

            System.out.println(
                    "------------------------------------------"
            );
        }
    }

    private static void simulateTampering() {

        System.out.println();
        System.out.println(
                "========== TAMPERING SIMULATION =========="
        );

        List<com.rupeeblockchain.model.Block>
                blockchain =
                blockchainService
                        .getBlockchain();

        if (blockchain.size() <= 1) {

            System.out.println(
                    "No mined transaction blocks available."
            );

            return;
        }

        blockchainService
                .displayBlockchain();

        System.out.print(
                "Enter block index to tamper: "
        );

        String blockInput =
                scanner.nextLine();

        System.out.print(
                "Enter transaction index: "
        );

        String transactionInput =
                scanner.nextLine();

        System.out.print(
                "Enter new amount: ₹"
        );

        String amountInput =
                scanner.nextLine();

        try {

            int blockIndex =
                    Integer.parseInt(
                            blockInput
                    );

            int transactionIndex =
                    Integer.parseInt(
                            transactionInput
                    );

            BigDecimal newAmount =
                    new BigDecimal(
                            amountInput
                    );

            boolean success =
                    blockchainService
                            .simulateTampering(
                                    blockIndex,
                                    transactionIndex,
                                    newAmount
                            );

            if (success) {

                System.out.println();
                System.out.println(
                        "Tampering simulation completed."
                );

                System.out.println(
                        "Transaction amount was deliberately changed."
                );

                System.out.println(
                        "Run blockchain validation to detect it."
                );

            } else {

                System.out.println(
                        "Tampering simulation failed."
                );
            }

        } catch (NumberFormatException e) {

            System.out.println(
                    "Invalid input."
            );
        }
    }

    private static void restoreBlockchain() {

        boolean restored =
                blockchainService
                        .restoreBlockchain();

        System.out.println();

        if (restored) {

            System.out.println(
                    "Blockchain restored successfully."
            );

        } else {

            System.out.println(
                    "No backup is available."
            );
        }
    }

    private static void exportBlockchain() {

        blockchainService
                .getFileService()
                .exportBlockchain(
                        blockchainService
                                .getBlockchain()
                );

        System.out.println();
        System.out.println(
                "Blockchain exported successfully."
        );

        System.out.println(
                "File: "
                        + blockchainService
                        .getFileService()
                        .getBlockchainExportPath()
        );
    }

    private static void createBlockchainBackup() {

        blockchainService
                .getFileService()
                .createBlockchainBackup(
                        blockchainService
                                .getBlockchain()
                );

        System.out.println();
        System.out.println(
                "Blockchain backup created successfully."
        );

        System.out.println(
                "File: "
                        + blockchainService
                        .getFileService()
                        .getBlockchainBackupPath()
        );
    }

    private static void viewTransactionLog() {

        blockchainService
                .getFileService()
                .readTransactionLog();
    }
}