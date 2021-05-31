package com.scopic.auction.integration;

import com.scopic.auction.dto.ItemDto;
import com.scopic.auction.dto.MoneyDto;
import com.scopic.auction.dto.SettingsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseIntegrationTest {

    @Autowired
    protected TestRestTemplate testRestTemplate;

    protected ResponseEntity<String> setMaxBidAmount(Number value, String username) {
        final var newSettings = new SettingsDto();
        newSettings.maxBidAmount = new MoneyDto(value, "USD");
        final var response = testRestTemplate.exchange(
                "/user/{user}/settings",
                HttpMethod.PUT,
                new HttpEntity<>(newSettings),
                String.class,
                username
        );
        return response;
    }

    protected void setupHeaders(String username) {
        testRestTemplate.getRestTemplate().getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set(HttpHeaders.USER_AGENT, username);//Set the header for each request
            return execution.execute(request, body);
        });
    }

    protected ItemDto fetchItemById(String itemId) {
        return testRestTemplate.getForEntity("/item/" + itemId, ItemDto.class).getBody();
    }

    protected String createItem(String name, String description) {
        final var item = new ItemDto(null, name, description);
        final var result = testRestTemplate.postForEntity(
                "/item",
                item,
                String.class
        );
        final var body = result.getBody();
        assertNotNull(body);
        assertNotNull(UUID.fromString(body));
        return body;
    }
}
