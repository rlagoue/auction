package com.scopic.auction.utils;

import java.util.Optional;

public class ThreadLocalStorage {
    private static final InheritableThreadLocal<ThreadLocalStorage> data = new InheritableThreadLocal<>();

    public final String username;

    public ThreadLocalStorage(String username) {
        super();
        this.username = username;
    }

    public static void set(ThreadLocalStorage data) {
        ThreadLocalStorage.data.set(data);
    }

    public static Optional<ThreadLocalStorage> get() {
        return Optional.ofNullable(ThreadLocalStorage.data.get());
    }

    public static void clear() {
        ThreadLocalStorage.data.set(null);
    }

}
