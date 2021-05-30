package com.scopic.auction.e2e;

import com.scopic.auction.dto.ItemDto;
import com.scopic.auction.dto.ItemFetchDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryTest.class);
    @Autowired
    private TestRestTemplate testRestTemplate;

    @BeforeEach
    public void createHeaders() {
        testRestTemplate.getRestTemplate().getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set(HttpHeaders.USER_AGENT, "admin");//Set the header for each request
            return execution.execute(request, body);
        });

    }

    @Test
    void crudItemTest() {
        final var name1 = "mug";
        final var description1 = "mug from sultan Njoya";
        final var id1 = createItem(name1, description1);

        final var item = testRestTemplate.getForEntity("/item/" + id1, ItemDto.class).getBody();
        assertEquals(id1, Objects.requireNonNull(item).id);
        assertEquals(name1, item.name);
        assertEquals(description1, item.description);

    }

    @Test
    void fetchItemsTest() {
        Set<String> names = new HashSet<>();
        for (int index = 0; index < 24; index++) {
            final var name = "name_" + index;
            createItem(name, "description_" + index);
            names.add(name);
        }
        var result = fetchItems(0).getBody();
        LOGGER.info("total number of items: " + Objects.requireNonNull(result).totalCount);
        LOGGER.info("first page items: " + result.items);
        assertTrue(result.totalCount >= 24);
        var numberOfPages = result.totalCount / 10 + 1;
        LOGGER.info("number of pages: " + numberOfPages);

        Set<ItemDto> items = new HashSet<>((int) result.totalCount);
        items.addAll(result.items);


        for (int index = 1; index < numberOfPages; index++) {
            items.addAll(Objects.requireNonNull(fetchItems(index).getBody()).items);
        }
        final var allNames = items.stream().map(item -> item.name).collect(Collectors.toSet());
        assertTrue(allNames.containsAll(names));

    }

    private ResponseEntity<ItemFetchDto> fetchItems(int pageIndex) {
        return testRestTemplate.getForEntity(
                "/item?pageIndex={pageIndex}",
                ItemFetchDto.class,
                pageIndex
        );
    }

    private String createItem(String name, String description) {
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
