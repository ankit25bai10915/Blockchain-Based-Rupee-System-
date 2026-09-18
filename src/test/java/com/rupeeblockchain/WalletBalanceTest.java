package com.rupeeblockchain;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.rupeeblockchain.model.User;
import com.rupeeblockchain.service.AuthService;

public class WalletBalanceTest {

    @Test
    void testWalletExists() {

        AuthService authService = new AuthService();

        User user = authService.authenticateUser(
                "U1001",
                "alice123"
        );

        assertNotNull(user);
        assertNotNull(user.getWallet());
        assertNotNull(user.getWallet().getBalance());

        assertTrue(
                user.getWallet().getBalance()
                        .compareTo(BigDecimal.ZERO) >= 0
        );
    }
}