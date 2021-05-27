package com.scopic.auction.domain;

import com.scopic.auction.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.scopic.auction.utils.Whitebox.getFieldValue;
import static com.scopic.auction.utils.Whitebox.setFieldValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {

    private User objectToTest;

    @BeforeEach
    void setUp() {
        objectToTest = new User();
    }

    @Test
    void userTest() {
        final String username = "username";
        objectToTest = new User(username);

        assertEquals(username, getFieldValue(objectToTest, "username"));
    }

    @Test
    void toDtoTest() {
        final String username = "user1";
        setFieldValue(objectToTest, "username", username);

        final UserDto userDto = objectToTest.toDto();

        assertEquals(username, userDto.username);
    }
}