package com.rupeeblockchain;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.rupeeblockchain.service.AuthService;
import com.rupeeblockchain.service.BlockchainService;
import com.rupeeblockchain.service.TransactionService;

public class BlockchainValidationTest {

    @Test
    void testBlockchainServiceCreation() {

        AuthService authService = new AuthService();

        TransactionService transactionService =
                new TransactionService(authService);

        BlockchainService blockchainService =
                new BlockchainService(
                        authService,
                        transactionService
                );

        assertNotNull(blockchainService);
        assertTrue(blockchainService.validateBlockchain());
    }
}