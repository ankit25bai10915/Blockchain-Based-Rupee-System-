package com.rupeeblockchain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Transaction {

    private String transactionId;
    private String senderId;
    private String receiverId;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private TransactionStatus status;

    public Transaction(
            String transactionId,
            String senderId,
            String receiverId,
            BigDecimal amount) {

        this(
                transactionId,
                senderId,
                receiverId,
                amount,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                TransactionStatus.PENDING
        );
    }

    public Transaction(
            String transactionId,
            String senderId,
            String receiverId,
            BigDecimal amount,
            LocalDateTime timestamp,
            TransactionStatus status) {

        this.transactionId = transactionId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.timestamp = timestamp != null
                ? timestamp.truncatedTo(ChronoUnit.SECONDS)
                : null;
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setReceiverId(
            String receiverId) {

        this.receiverId = receiverId;
    }

    public void setAmount(
            BigDecimal amount) {

        this.amount = amount;
    }

    public void setStatus(
            TransactionStatus status) {

        this.status = status;
    }

    @Override
    public String toString() {

        return "Transaction ID : "
                + transactionId
                + "\nSender         : "
                + senderId
                + "\nReceiver       : "
                + receiverId
                + "\nAmount         : ₹"
                + amount
                + "\nTime           : "
                + timestamp
                + "\nStatus         : "
                + status;
    }
}