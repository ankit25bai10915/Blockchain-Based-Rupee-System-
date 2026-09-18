package com.rupeeblockchain.model;

import java.math.BigDecimal;

public class Wallet {

    private String walletId;
    private String ownerId;
    private BigDecimal balance;

    public Wallet(String walletId, String ownerId, BigDecimal balance) {
        this.walletId = walletId;
        this.ownerId = ownerId;
        this.balance = balance;
    }

    public String getWalletId() {
        return walletId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }

    public boolean withdraw(BigDecimal amount) {

        if (amount.compareTo(balance) > 0) {
            return false;
        }

        balance = balance.subtract(amount);
        return true;
    }
}