package com.rupeeblockchain.model;

import java.math.BigDecimal;

public class User {

    private String userId;
    private String name;
    private String email;
    private String password;
    private Wallet wallet;

    public User(
            String userId,
            String name,
            String email,
            String password,
            Wallet wallet) {

        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.wallet = wallet;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public BigDecimal getBalance() {
        return wallet.getBalance();
    }
}