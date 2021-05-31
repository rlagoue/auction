package com.scopic.auction.integration;

import com.scopic.auction.dto.SettingsDto;
import com.scopic.auction.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class UserIntegrationTest extends BaseIntegrationTest {

    @Test
    void authWithAllUsers() {
        assertEquals("token", testRestTemplate.postForObject(
                "/authenticate",
                new UserDto("admin"),
                String.class
        ));
        assertEquals("token", testRestTemplate.postForObject(
                "/authenticate",
                new UserDto("user1"),
                String.class
        ));
        assertEquals("token", testRestTemplate.postForObject(
                "/authenticate",
                new UserDto("user2"),
                String.class
        ));
        final var user3 = testRestTemplate.postForObject(
                "/authenticate",
                new UserDto("user3"),
                String.class
        );
        assertNull(user3);
    }

    @Test
    void crudSettingsTest() {
        assertCurrentMaxBidAmountIs(0);

        final ResponseEntity<String> response = setMaxBidAmount(100, "userCrudSettings");
        assertEquals("success", response.getBody());

        assertCurrentMaxBidAmountIs(100);
    }

    private void assertCurrentMaxBidAmountIs(int expectedValue) {
        final SettingsDto settings = getSettings();
        assertNotNull(settings);
        assertNotNull(settings.maxBidAmount);
        assertEquals(expectedValue, settings.maxBidAmount.value);
        assertEquals("USD", settings.maxBidAmount.currency);
    }

    private SettingsDto getSettings() {
        return testRestTemplate.getForEntity(
                "/user/userCrudSettings/settings",
                SettingsDto.class
        ).getBody();
    }
}
