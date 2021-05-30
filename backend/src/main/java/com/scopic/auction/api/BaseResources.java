package com.scopic.auction.api;

import com.scopic.auction.utils.ThreadLocalStorage;

public abstract class BaseResources {

    protected String getCurrentUsername() {
        return ThreadLocalStorage.get().orElseThrow().username;
    }
}
