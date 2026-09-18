package com.rupeeblockchain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;

import com.rupeeblockchain.service.HashUtil;

public class HashUtilTest {

    @Test
    public void sameInputShouldProduceSameHash() {

        String firstHash =
                HashUtil.sha256(
                        "Blockchain Rupee System"
                );

        String secondHash =
                HashUtil.sha256(
                        "Blockchain Rupee System"
                );

        assertEquals(
                firstHash,
                secondHash
        );
    }

    @Test
    public void differentInputShouldProduceDifferentHash() {

        String firstHash =
                HashUtil.sha256(
                        "Transaction 100"
                );

        String secondHash =
                HashUtil.sha256(
                        "Transaction 101"
                );

        assertNotEquals(
                firstHash,
                secondHash
        );
    }

    @Test
    public void hashShouldHave64HexCharacters() {

        String hash =
                HashUtil.sha256(
                        "Test Data"
                );

        assertEquals(
                64,
                hash.length()
        );
    }
}
