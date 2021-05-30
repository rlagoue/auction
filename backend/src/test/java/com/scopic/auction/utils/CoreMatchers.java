package com.scopic.auction.utils;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.time.Duration;
import java.time.LocalDateTime;

public class CoreMatchers {

    public static Matcher<LocalDateTime> notOlderThan(long millis) {
        return new BaseMatcher<>() {

            @Override
            public boolean matches(Object actual) {
                return Duration.between(
                        LocalDateTime.now(),
                        (LocalDateTime) actual
                ).abs().toMillis() <= millis;
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }

}
