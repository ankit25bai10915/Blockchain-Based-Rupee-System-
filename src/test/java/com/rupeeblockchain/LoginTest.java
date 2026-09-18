package com.rupeeblockchain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

import com.rupeeblockchain.model.User;
import com.rupeeblockchain.service.AuthService;

public class LoginTest {

    @Test
    void testUserLogin() {

        AuthService authService = new AuthService();

        User user = authService.authenticateUser(
                "U1001",
                "alice123"
        );

        assertNotNull(user);
        assertEquals("Alice", user.getName());
    }

    @Test
    void testInvalidLogin() {

        AuthService authService = new AuthService();

        User user = authService.authenticateUser(
                "U1001",
                "wrongpassword"
        );

        assertNull(user);
    }
}