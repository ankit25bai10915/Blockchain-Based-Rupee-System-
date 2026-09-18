package com.rupeeblockchain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.rupeeblockchain.service.HashUtil;

public class Block {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private int index;
    private LocalDateTime timestamp;
    private List<Transaction> transactions;
    private String previousHash;
    private String hash;
    private long nonce;

    public Block(
            int index,
            List<Transaction> transactions,
            String previousHash) {

        this.index = index;
        this.timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        this.transactions = new ArrayList<>(transactions);
        this.previousHash = previousHash;
        this.nonce = 0;

        this.hash = calculateHash();
    }

    /*
     * Constructor used when loading a block
     * from the database.
     */
    public Block(
            int index,
            LocalDateTime timestamp,
            List<Transaction> transactions,
            String previousHash,
            String hash,
            long nonce) {

        this.index = index;
        this.timestamp = timestamp != null
                ? timestamp.truncatedTo(ChronoUnit.SECONDS)
                : null;
        this.transactions = new ArrayList<>(transactions);
        this.previousHash = previousHash;
        this.hash = hash;
        this.nonce = nonce;
    }

    /*
     * Copy constructor.
     */
    public Block(Block original) {

        this.index = original.index;
        this.timestamp = original.timestamp;
        this.previousHash = original.previousHash;
        this.hash = original.hash;
        this.nonce = original.nonce;

        this.transactions = new ArrayList<>();

        for (Transaction transaction : original.transactions) {

            Transaction copiedTransaction =
                    new Transaction(
                            transaction.getTransactionId(),
                            transaction.getSenderId(),
                            transaction.getReceiverId(),
                            transaction.getAmount(),
                            transaction.getTimestamp(),
                            transaction.getStatus()
                    );

            this.transactions.add(copiedTransaction);
        }
    }

    public int getIndex() {
        return index;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getHash() {
        return hash;
    }

    public long getNonce() {
        return nonce;
    }

    /*
     * Converts the amount into a stable representation.
     *
     * This is extremely important because the block hash
     * must be identical before and after loading from H2.
     */
    private String formatAmount(BigDecimal amount) {

        if (amount == null) {
            return "0.00";
        }

        return amount
                .setScale(2, RoundingMode.UNNECESSARY)
                .toPlainString();
    }

    /*
     * Converts the timestamp into a stable representation.
     *
     * This is extremely important because the block hash
     * must be identical before and after loading from H2.
     */
    private String formatTimestamp(LocalDateTime timestamp) {

        if (timestamp == null) {
            return "";
        }

        return timestamp.format(TIMESTAMP_FORMATTER);
    }

    /*
     * Builds the exact data used for hashing.
     *
     * Every value is converted into a deterministic String.
     */
    private String buildHashData(long candidateNonce) {

        StringBuilder data = new StringBuilder();

        data.append(index);
        data.append(formatTimestamp(timestamp));
        data.append(previousHash);
        data.append(candidateNonce);

        for (Transaction transaction : transactions) {

            data.append(transaction.getTransactionId());

            data.append(transaction.getSenderId());

            data.append(transaction.getReceiverId());

            data.append(
                    formatAmount(
                            transaction.getAmount()
                    )
            );

            data.append(transaction.getStatus());
        }

        return data.toString();
    }

    /*
     * Calculates the current block hash.
     */
    public String calculateHash() {

        return HashUtil.sha256(
                buildHashData(nonce)
        );
    }

    /*
     * Calculates a hash using a candidate nonce
     * without modifying the block.
     */
    public String calculateHashForNonce(
            long candidateNonce) {

        return HashUtil.sha256(
                buildHashData(candidateNonce)
        );
    }

    /*
     * Applies the winning mining result.
     */
    public void applyMiningResult(
            long winningNonce,
            String winningHash) {

        this.nonce = winningNonce;
        this.hash = winningHash;
    }

    /*
     * Single-threaded proof-of-work mining.
     */
    public void mineBlock(int difficulty) {

        String target =
                "0".repeat(difficulty);

        System.out.println();

        System.out.println(
                "Mining Block #" + index + "..."
        );

        long startTime =
                System.currentTimeMillis();

        do {

            nonce++;

            hash = calculateHash();

        } while (!hash.startsWith(target));

        long endTime =
                System.currentTimeMillis();

        System.out.println(
                "Block mined successfully!"
        );

        System.out.println(
                "Nonce       : " + nonce
        );

        System.out.println(
                "Hash        : " + hash
        );

        System.out.println(
                "Mining time : "
                        + (endTime - startTime)
                        + " ms"
        );
    }

    /*
     * Checks whether the stored hash matches
     * the hash calculated from the current data.
     */
    public boolean isValid() {

        return hash.equals(
                calculateHash()
        );
    }

    /*
     * Deliberately changes a transaction amount
     * without changing the stored block hash.
     *
     * Used for tampering demonstration.
     */
    public boolean tamperTransaction(
            int transactionIndex,
            BigDecimal newAmount) {

        if (transactionIndex < 0
                || transactionIndex >= transactions.size()) {

            return false;
        }

        Transaction transaction =
                transactions.get(transactionIndex);

        transaction.setAmount(newAmount);

        return true;
    }

    @Override
    public String toString() {

        return "Block #" + index +

                "\nTimestamp    : " + timestamp +

                "\nTransactions : " + transactions.size() +

                "\nPrevious Hash: " + previousHash +

                "\nNonce        : " + nonce +

                "\nHash         : " + hash;
    }
}