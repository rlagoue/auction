package com.scopic.auction.e2e;

import com.scopic.auction.dto.MoneyDto;
import com.scopic.auction.dto.SettingsDto;
import com.scopic.auction.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserTest {

    @Autowired
    private TestRestTemplate testRestTemplate;


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

        final var newSettings = new SettingsDto();
        newSettings.maxBidAmount = new MoneyDto(100, "USD");
        final var response = testRestTemplate.exchange(
                "/user/userCrudSettings/settings",
                HttpMethod.PUT,
                new HttpEntity<>(newSettings),
                String.class
        );
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
