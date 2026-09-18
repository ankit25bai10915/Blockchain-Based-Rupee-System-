package com.rupeeblockchain;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import com.rupeeblockchain.model.Transaction;
import com.rupeeblockchain.model.TransactionStatus;

public class TransactionTest {

    @Test
    public void transactionShouldStoreCorrectDetails() {

        Transaction transaction =
                new Transaction(
                        "TXTEST1",
                        "U1001",
                        "U1002",
                        new BigDecimal("500.00")
                );

        assertEquals(
                "TXTEST1",
                transaction.getTransactionId()
        );

        assertEquals(
                "U1001",
                transaction.getSenderId()
        );

        assertEquals(
                "U1002",
                transaction.getReceiverId()
        );

        assertEquals(
                new BigDecimal("500.00"),
                transaction.getAmount()
        );

        assertEquals(
                TransactionStatus.PENDING,
                transaction.getStatus()
        );

        assertNotNull(
                transaction.getTimestamp()
        );
    }

    @Test
    public void transactionStatusShouldChangeToConfirmed() {

        Transaction transaction =
                new Transaction(
                        "TXTEST2",
                        "U1001",
                        "U1002",
                        new BigDecimal("250.00")
                );

        transaction.setStatus(
                TransactionStatus.CONFIRMED
        );

        assertEquals(
                TransactionStatus.CONFIRMED,
                transaction.getStatus()
        );
    }
}