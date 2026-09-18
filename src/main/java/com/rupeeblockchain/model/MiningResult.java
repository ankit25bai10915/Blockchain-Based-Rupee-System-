package com.rupeeblockchain.model;

public class MiningResult {

    private final boolean successful;
    private final long nonce;
    private final String hash;

    public MiningResult(
            boolean successful,
            long nonce,
            String hash) {

        this.successful = successful;
        this.nonce = nonce;
        this.hash = hash;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public long getNonce() {
        return nonce;
    }

    public String getHash() {
        return hash;
    }
}