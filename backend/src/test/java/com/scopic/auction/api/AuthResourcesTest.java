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
        final String token = objectToTest.authenticate(
                new UserDto("admin", "admin")
        );

        assertEquals("token", token);
    }

    @Test
    void authenticateAsUser1Test() {
        final String token = objectToTest.authenticate(
                new UserDto("user1", "password")
        );

        assertEquals("token", token);
    }

    @Test
    void authenticateAsUser2Test() {
        final String token = objectToTest.authenticate(
                new UserDto("user2", "password")
        );

        assertEquals("token", token);
    }

    @Test
    void authenticateWithFailureTest() {
        final String token = objectToTest.authenticate(
                new UserDto("username", "password")
        );

        assertEquals("", token);
    }
}