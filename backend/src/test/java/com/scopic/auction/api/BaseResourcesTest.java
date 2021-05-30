package com.scopic.auction.api;

import com.scopic.auction.utils.ThreadLocalStorage;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseResourcesTest {

    public static final String CURRENT_USER = "user1";

    @BeforeEach
    void setUp() {
        ThreadLocalStorage.set(new ThreadLocalStorage(CURRENT_USER));
    }
}
