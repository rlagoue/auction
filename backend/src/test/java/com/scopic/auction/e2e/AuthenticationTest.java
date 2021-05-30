package com.scopic.auction.e2e;

import com.scopic.auction.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;


    @Test
    void authWithAllUsers() {
        testRestTemplate.postForObject(
                "/authenticate",
                new UserDto("admin"),
                String.class
        ).equals("token");
        testRestTemplate.postForObject(
                "/authenticate",
                new UserDto("user1"),
                String.class
        ).equals("token");
        testRestTemplate.postForObject(
                "/authenticate",
                new UserDto("user2"),
                String.class
        ).equals("token");
        final var user3 = testRestTemplate.postForObject(
                "/authenticate",
                new UserDto("user3"),
                String.class
        );
        assertNull(user3);
    }

}
