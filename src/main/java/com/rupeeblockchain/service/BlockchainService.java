package com.rupeeblockchain.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.rupeeblockchain.database.BlockRepository;
import com.rupeeblockchain.database.TransactionRepository;
import com.rupeeblockchain.model.Block;
import com.rupeeblockchain.model.MiningResult;
import com.rupeeblockchain.model.Transaction;
import com.rupeeblockchain.model.TransactionStatus;
import com.rupeeblockchain.model.User;

public class BlockchainService {

    private final AuthService authService;
    private final TransactionService transactionService;

    private final TransactionRepository
            transactionRepository;

    private final BlockRepository
            blockRepository;

    private final FileService fileService;

    private final MultithreadedMiningService
            multithreadedMiningService;

    private final List<Block> blockchain;

    private List<Block> backupBlockchain;

    private static final int MINING_DIFFICULTY = 3;

    private static final int MINING_THREAD_COUNT = 4;

    public BlockchainService(
            AuthService authService,
            TransactionService transactionService) {

        this.authService =
                authService;

        this.transactionService =
                transactionService;

        this.transactionRepository =
                transactionService
                        .getTransactionRepository();

        this.blockRepository =
                new BlockRepository();

        this.fileService =
                transactionService
                        .getFileService();

        this.multithreadedMiningService =
                new MultithreadedMiningService();

        this.blockchain =
                new ArrayList<>();

        loadBlockchainFromDatabase();
    }

    private void loadBlockchainFromDatabase() {

        List<Block> databaseBlocks =
                blockRepository.findAllBlocks();

        if (databaseBlocks.isEmpty()) {

            createGenesisBlock();

        } else {

            blockchain.clear();

            blockchain.addAll(
                    databaseBlocks
            );
        }
    }

    private void createGenesisBlock() {

        Block genesisBlock =
                new Block(
                        0,
                        new ArrayList<>(),
                        "0"
                );

        blockchain.add(
                genesisBlock
        );

        blockRepository.saveBlock(
                genesisBlock
        );
    }

    /*
     * Existing single-threaded mining.
     */
    public void minePendingTransactions() {

        minePendingTransactionsInternal(false);
    }

    /*
     * New multithreaded mining.
     */
    public void minePendingTransactionsMultithreaded() {

        minePendingTransactionsInternal(true);
    }

    private void minePendingTransactionsInternal(
            boolean useMultipleThreads) {

        List<Transaction> pending =
                transactionService
                        .getAllPendingTransactions();

        if (pending.isEmpty()) {

            System.out.println();
            System.out.println(
                    "No pending transactions to mine."
            );

            return;
        }

        List<Transaction> validTransactions =
                new ArrayList<>();

        for (Transaction transaction :
                pending) {

            User sender =
                    authService.findUserById(
                            transaction.getSenderId()
                    );

            User receiver =
                    authService.findUserById(
                            transaction.getReceiverId()
                    );

            if (sender == null
                    || receiver == null) {

                System.out.println(
                        "Invalid transaction: "
                                + transaction.getTransactionId()
                );

                continue;
            }

            BigDecimal amount =
                    transaction.getAmount();

            boolean withdrawn =
                    sender.getWallet()
                            .withdraw(amount);

            if (!withdrawn) {

                System.out.println(
                        "Insufficient balance for "
                                + transaction.getTransactionId()
                );

                continue;
            }

            receiver.getWallet()
                    .deposit(amount);

            transaction.setStatus(
                    TransactionStatus.CONFIRMED
            );

            transactionRepository
                    .updateTransactionStatus(
                            transaction.getTransactionId(),
                            TransactionStatus.CONFIRMED
                    );

            authService
                    .getWalletRepository()
                    .updateBalance(
                            sender.getUserId(),
                            sender.getWallet()
                                    .getBalance()
                    );

            authService
                    .getWalletRepository()
                    .updateBalance(
                            receiver.getUserId(),
                            receiver.getWallet()
                                    .getBalance()
                    );

            validTransactions.add(
                    transaction
            );
        }

        if (validTransactions.isEmpty()) {

            System.out.println();

            System.out.println(
                    "No valid transactions available "
                            + "for mining."
            );

            return;
        }

        Block previousBlock =
                blockchain.get(
                        blockchain.size() - 1
                );

        Block newBlock =
                new Block(
                        blockchain.size(),
                        validTransactions,
                        previousBlock.getHash()
                );

        System.out.println();
        System.out.println(
                "=========================================="
        );

        if (useMultipleThreads) {

            System.out.println(
                    "     MULTITHREADED MINING MODE"
            );

        } else {

            System.out.println(
                    "       SINGLE-THREADED MINING"
            );
        }

        System.out.println(
                "=========================================="
        );

        MiningResult miningResult;

        if (useMultipleThreads) {

            miningResult =
                    multithreadedMiningService
                            .mineBlock(
                                    newBlock,
                                    MINING_DIFFICULTY,
                                    MINING_THREAD_COUNT
                            );

        } else {

            newBlock.mineBlock(
                    MINING_DIFFICULTY
            );

            miningResult =
                    new MiningResult(
                            true,
                            newBlock.getNonce(),
                            newBlock.getHash()
                    );
        }

        if (!miningResult.isSuccessful()) {

            System.out.println(
                    "Mining failed."
            );

            return;
        }

        blockRepository.saveBlock(
                newBlock
        );

        blockchain.add(
                newBlock
        );

        transactionService
                .clearConfirmedTransactions();

        /*
         * Create a human-readable blockchain
         * backup after successful mining.
         */
        fileService.createBlockchainBackup(
                blockchain
        );

        System.out.println();
        System.out.println(
                "======================================"
        );

        System.out.println(
                "        TRANSACTION CONFIRMED"
        );

        System.out.println(
                "======================================"
        );

        System.out.println(
                "Block #"
                        + newBlock.getIndex()
                        + " added to blockchain."
        );

        System.out.println(
                "Transactions mined: "
                        + validTransactions.size()
        );

        System.out.println(
                "Final Hash:"
        );

        System.out.println(
                newBlock.getHash()
        );

        System.out.println(
                "Nonce: "
                        + newBlock.getNonce()
        );

        System.out.println(
                "Blockchain backup created at:"
        );

        System.out.println(
                fileService
                        .getBlockchainBackupPath()
        );

        System.out.println(
                "======================================"
        );
    }

    private void createBackup() {

        backupBlockchain =
                new ArrayList<>();

        for (Block block :
                blockchain) {

            backupBlockchain.add(
                    new Block(block)
            );
        }
    }

    public boolean simulateTampering(
            int blockIndex,
            int transactionIndex,
            BigDecimal newAmount) {

        if (blockIndex <= 0
                || blockIndex >= blockchain.size()) {

            return false;
        }

        if (newAmount.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            return false;
        }

        createBackup();

        Block block =
                blockchain.get(
                        blockIndex
                );

        return block.tamperTransaction(
                transactionIndex,
                newAmount
        );
    }

    public boolean restoreBlockchain() {

        if (backupBlockchain == null) {
            return false;
        }

        blockchain.clear();

        for (Block block :
                backupBlockchain) {

            blockchain.add(
                    new Block(block)
            );
        }

        return true;
    }

    public List<Block> getBlockchain() {

        return new ArrayList<>(
                blockchain
        );
    }

    public boolean validateBlockchain() {

        System.out.println();
        System.out.println(
                "========== BLOCKCHAIN VALIDATION =========="
        );

        if (blockchain.isEmpty()) {

            System.out.println(
                    "Blockchain is empty."
            );

            return false;
        }

        for (int i = 0;
                i < blockchain.size();
                i++) {

            Block currentBlock =
                    blockchain.get(i);

            String recalculatedHash =
                    currentBlock.calculateHash();

            if (!currentBlock.getHash()
                    .equals(recalculatedHash)) {

                System.out.println();
                System.out.println(
                        "INVALID BLOCK DETECTED!"
                );

                System.out.println(
                        "Block Number: "
                                + currentBlock.getIndex()
                );

                System.out.println(
                        "Reason: Stored hash does not "
                                + "match calculated hash."
                );

                System.out.println();
                System.out.println(
                        "Stored Hash:"
                );

                System.out.println(
                        currentBlock.getHash()
                );

                System.out.println();
                System.out.println(
                        "Calculated Hash:"
                );

                System.out.println(
                        recalculatedHash
                );

                System.out.println(
                        "=========================================="
                );

                return false;
            }

            if (i > 0) {

                Block previousBlock =
                        blockchain.get(i - 1);

                if (!currentBlock
                        .getPreviousHash()
                        .equals(
                                previousBlock.getHash()
                        )) {

                    System.out.println();
                    System.out.println(
                            "BLOCKCHAIN LINK BROKEN!"
                    );

                    System.out.println(
                            "Block Number: "
                                    + currentBlock.getIndex()
                    );

                    System.out.println(
                            "Reason: Previous hash does not "
                                    + "match the previous block."
                    );

                    System.out.println(
                            "=========================================="
                    );

                    return false;
                }
            }

            System.out.println(
                    "Block #"
                            + currentBlock.getIndex()
                            + " : VALID"
            );
        }

        System.out.println();
        System.out.println(
                "Blockchain validation successful."
        );

        System.out.println(
                "All blocks are valid."
        );

        System.out.println(
                "No tampering detected."
        );

        System.out.println(
                "=========================================="
        );

        return true;
    }

    public void displayBlockchain() {

        System.out.println();
        System.out.println(
                "=========================================="
        );

        System.out.println(
                "              BLOCKCHAIN"
        );

        System.out.println(
                "=========================================="
        );

        for (Block block :
                blockchain) {

            System.out.println();
            System.out.println(
                    "------------------------------------------"
            );

            System.out.println(
                    block
            );

            if (!block.getTransactions()
                    .isEmpty()) {

                System.out.println();
                System.out.println(
                        "Transactions:"
                );

                for (Transaction transaction :
                        block.getTransactions()) {

                    System.out.println(
                            "  "
                                    + transaction
                                    .getTransactionId()
                                    + " | "
                                    + transaction
                                    .getSenderId()
                                    + " -> "
                                    + transaction
                                    .getReceiverId()
                                    + " | ₹"
                                    + transaction
                                    .getAmount()
                                    + " | "
                                    + transaction
                                    .getStatus()
                    );
                }
            }
        }

        System.out.println(
                "------------------------------------------"
        );

        System.out.println(
                "=========================================="
        );
    }

    public FileService getFileService() {

        return fileService;
    }
}