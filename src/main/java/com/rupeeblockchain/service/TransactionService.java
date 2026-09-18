package com.rupeeblockchain.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.rupeeblockchain.database.TransactionRepository;
import com.rupeeblockchain.model.Transaction;
import com.rupeeblockchain.model.TransactionStatus;
import com.rupeeblockchain.model.User;

public class TransactionService {

    private final AuthService authService;
    private final TransactionRepository transactionRepository;
    private final FileService fileService;

    private final List<Transaction> pendingTransactions;
    private final List<Transaction> confirmedTransactions;

    private int nextTransactionNumber = 1001;

    public TransactionService(
            AuthService authService) {

        this.authService = authService;

        this.transactionRepository =
                new TransactionRepository();

        this.fileService =
                new FileService();

        this.pendingTransactions =
                new ArrayList<>();

        this.confirmedTransactions =
                new ArrayList<>();

        loadTransactionsFromDatabase();
    }

    private void loadTransactionsFromDatabase() {

        pendingTransactions.clear();
        confirmedTransactions.clear();

        List<Transaction> pending =
                transactionRepository
                        .findTransactionsByStatus(
                                TransactionStatus.PENDING
                        );

        List<Transaction> confirmed =
                transactionRepository
                        .findTransactionsByStatus(
                                TransactionStatus.CONFIRMED
                        );

        pendingTransactions.addAll(pending);
        confirmedTransactions.addAll(confirmed);

        updateNextTransactionNumber();
    }

    private void updateNextTransactionNumber() {

        int highestNumber = 1000;

        List<Transaction> allTransactions =
                transactionRepository
                        .findAllTransactions();

        for (Transaction transaction :
                allTransactions) {

            String id =
                    transaction.getTransactionId();

            if (id.startsWith("TX")) {

                try {

                    int number =
                            Integer.parseInt(
                                    id.substring(2)
                            );

                    if (number > highestNumber) {
                        highestNumber = number;
                    }

                } catch (NumberFormatException ignored) {
                    // Ignore invalid transaction IDs
                }
            }
        }

        nextTransactionNumber =
                highestNumber + 1;
    }

    public Transaction createTransaction(
            String senderId,
            String receiverId,
            BigDecimal amount) {

        User sender =
                authService.findUserById(senderId);

        User receiver =
                authService.findUserById(receiverId);

        if (sender == null) {

            throw new IllegalArgumentException(
                    "Sender not found."
            );
        }

        if (receiver == null) {

            throw new IllegalArgumentException(
                    "Receiver not found."
            );
        }

        if (senderId.equals(receiverId)) {

            throw new IllegalArgumentException(
                    "Sender and receiver cannot be the same."
            );
        }

        if (amount.compareTo(
                BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Amount must be greater than zero."
            );
        }

        BigDecimal availableBalance =
                getAvailableBalance(senderId);

        if (amount.compareTo(
                availableBalance) > 0) {

            throw new IllegalArgumentException(
                    "Insufficient available balance."
            );
        }

        String transactionId =
                "TX" + nextTransactionNumber++;

        Transaction transaction =
                new Transaction(
                        transactionId,
                        senderId,
                        receiverId,
                        amount
                );

        transactionRepository.saveTransaction(
                transaction
        );

        pendingTransactions.add(transaction);

        /*
         * Write the transaction to the text log.
         */
        fileService.logTransaction(
                transaction
        );

        return transaction;
    }

    public BigDecimal getAvailableBalance(
            String userId) {

        User user =
                authService.findUserById(userId);

        if (user == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal reservedAmount =
                BigDecimal.ZERO;

        for (Transaction transaction :
                pendingTransactions) {

            if (transaction.getSenderId()
                    .equals(userId)

                    && transaction.getStatus()
                    == TransactionStatus.PENDING) {

                reservedAmount =
                        reservedAmount.add(
                                transaction.getAmount()
                        );
            }
        }

        return user.getBalance()
                .subtract(reservedAmount);
    }

    public List<Transaction>
    getPendingTransactionsForUser(
            String userId) {

        List<Transaction> result =
                new ArrayList<>();

        for (Transaction transaction :
                pendingTransactions) {

            if (transaction.getSenderId()
                    .equals(userId)

                    || transaction.getReceiverId()
                    .equals(userId)) {

                result.add(transaction);
            }
        }

        return result;
    }

    public List<Transaction>
    getAllPendingTransactions() {

        return new ArrayList<>(
                pendingTransactions
        );
    }

    public List<Transaction>
    getAllConfirmedTransactions() {

        return new ArrayList<>(
                confirmedTransactions
        );
    }

    public void clearConfirmedTransactions() {

        for (Transaction transaction :
                pendingTransactions) {

            if (transaction.getStatus()
                    == TransactionStatus.CONFIRMED) {

                transactionRepository
                        .updateTransactionStatus(
                                transaction
                                        .getTransactionId(),
                                TransactionStatus.CONFIRMED
                        );

                fileService.logTransaction(
                        transaction
                );

                confirmedTransactions.add(
                        transaction
                );
            }
        }

        pendingTransactions.removeIf(
                transaction ->
                        transaction.getStatus()
                                == TransactionStatus.CONFIRMED
        );
    }

    public List<Transaction>
    getTransactionHistory(
            String userId) {

        return transactionRepository
                .findTransactionsForUser(
                        userId
                );
    }

    public TransactionRepository
    getTransactionRepository() {

        return transactionRepository;
    }

    public FileService getFileService() {

        return fileService;
    }
}