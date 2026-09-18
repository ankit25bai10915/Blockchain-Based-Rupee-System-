package com.rupeeblockchain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.rupeeblockchain.model.User;
import com.rupeeblockchain.service.AuthService;

public class RegistrationTest {

    @Test
    void testUserRegistration() {

        AuthService authService = new AuthService();

        String userId = "TEST001";

        if (authService.findUserById(userId) == null) {

            boolean registered = authService.registerUser(
                    userId,
                    "Test User",
                    "test@gmail.com",
                    "test123"
            );

            assertTrue(registered);

            User user = authService.findUserById(userId);

            assertNotNull(user);
            assertEquals("Test User", user.getName());
            assertEquals("test@gmail.com", user.getEmail());
        }
    }
}