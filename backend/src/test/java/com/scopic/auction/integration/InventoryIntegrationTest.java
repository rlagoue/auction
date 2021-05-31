package com.scopic.auction.integration;

import com.scopic.auction.dto.ItemDto;
import com.scopic.auction.dto.ItemFetchDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class InventoryIntegrationTest extends BaseIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryIntegrationTest.class);

    @BeforeEach
    public void createHeaders() {
        setupHeaders("admin");
    }

    @Test
    void crudItemTest() {
        final var name1 = "mug";
        final var description1 = "mug from sultan Njoya";
        final var id1 = createItem(name1, description1);

        final ItemDto item = fetchItemById(id1);
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


}
