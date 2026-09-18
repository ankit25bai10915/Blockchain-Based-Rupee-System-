package com.rupeeblockchain;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class AppTest {

    @Test
    public void applicationTest() {

        boolean applicationReady = true;

        assertTrue(
                applicationReady,
                "Application should be ready for testing."
        );
    }
}