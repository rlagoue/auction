package com.scopic.auction.api;

import com.scopic.auction.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthResourcesTest {

    private AuthResources objectToTest;

    @BeforeEach
    void setUp() {
        objectToTest = new AuthResources();
    }

    @Test
    void authenticateAsAdminTest() {
        final UserDto userData = new UserDto()
                .setUsername("admin")
                .setPassword("password");
        final String token = objectToTest.authenticate(userData);

        assertEquals("token", token);
    }

    @Test
    void authenticateAsUser1Test() {
        final UserDto userData = new UserDto()
                .setUsername("user1")
                .setPassword("password");
        final String token = objectToTest.authenticate(userData);

        assertEquals("token", token);
    }

    @Test
    void authenticateAsUser2Test() {
        final UserDto userData = new UserDto()
                .setUsername("user2")
                .setPassword("password");
        final String token = objectToTest.authenticate(userData);

        assertEquals("token", token);
    }

    @Test
    void authenticateWithFailureTest() {
        final UserDto userData = new UserDto()
                .setUsername("username")
                .setPassword("password");
        final String token = objectToTest.authenticate(userData);

        assertEquals("", token);
    }
}