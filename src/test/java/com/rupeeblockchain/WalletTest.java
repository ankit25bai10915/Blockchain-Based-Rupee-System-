package com.rupeeblockchain;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.rupeeblockchain.model.Wallet;

public class WalletTest {

    @Test
    public void depositShouldIncreaseBalance() {

        Wallet wallet =
                new Wallet(
                        "WTEST1",
                        "UTEST1",
                        new BigDecimal("1000.00")
                );

        wallet.deposit(
                new BigDecimal("500.00")
        );

        assertEquals(
                new BigDecimal("1500.00"),
                wallet.getBalance()
        );
    }

    @Test
    public void withdrawShouldDecreaseBalance() {

        Wallet wallet =
                new Wallet(
                        "WTEST2",
                        "UTEST2",
                        new BigDecimal("1000.00")
                );

        boolean result =
                wallet.withdraw(
                        new BigDecimal("300.00")
                );

        assertTrue(result);

        assertEquals(
                new BigDecimal("700.00"),
                wallet.getBalance()
        );
    }

    @Test
    public void withdrawShouldFailWhenBalanceIsInsufficient() {

        Wallet wallet =
                new Wallet(
                        "WTEST3",
                        "UTEST3",
                        new BigDecimal("1000.00")
                );

        boolean result =
                wallet.withdraw(
                        new BigDecimal("1500.00")
                );

        assertFalse(result);

        assertEquals(
                new BigDecimal("1000.00"),
                wallet.getBalance()
        );
    }
}