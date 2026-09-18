package com.rupeeblockchain;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.rupeeblockchain.model.Block;
import com.rupeeblockchain.model.Transaction;

public class BlockTest {

    @Test
    public void newBlockShouldBeValid() {

        Transaction transaction =
                new Transaction(
                        "TXBLOCK1",
                        "U1001",
                        "U1002",
                        new BigDecimal("100.00")
                );

        Block block =
                new Block(
                        1,
                        List.of(transaction),
                        "previous-hash"
                );

        assertTrue(
                block.isValid()
        );
    }

    @Test
    public void changingTransactionShouldInvalidateBlock() {

        Transaction transaction =
                new Transaction(
                        "TXBLOCK2",
                        "U1001",
                        "U1002",
                        new BigDecimal("100.00")
                );

        Block block =
                new Block(
                        1,
                        List.of(transaction),
                        "previous-hash"
                );

        assertTrue(
                block.isValid()
        );

        boolean tampered =
                block.tamperTransaction(
                        0,
                        new BigDecimal("1000.00")
                );

        assertTrue(
                tampered
        );

        assertFalse(
                block.isValid()
        );
    }

    @Test
    public void invalidTransactionIndexShouldFailTampering() {

        Transaction transaction =
                new Transaction(
                        "TXBLOCK3",
                        "U1001",
                        "U1002",
                        new BigDecimal("100.00")
                );

        Block block =
                new Block(
                        1,
                        List.of(transaction),
                        "previous-hash"
                );

        boolean result =
                block.tamperTransaction(
                        5,
                        new BigDecimal("500.00")
                );

        assertFalse(result);
    }

    @Test
    public void candidateNonceHashShouldNotChangeBlock() {

        Transaction transaction =
                new Transaction(
                        "TXBLOCK4",
                        "U1001",
                        "U1002",
                        new BigDecimal("100.00")
                );

        Block block =
                new Block(
                        1,
                        List.of(transaction),
                        "previous-hash"
                );

        long originalNonce =
                block.getNonce();

        String originalHash =
                block.getHash();

        block.calculateHashForNonce(
                5000
        );

        assertEquals(
                originalNonce,
                block.getNonce()
        );

        assertEquals(
                originalHash,
                block.getHash()
        );
    }
}
