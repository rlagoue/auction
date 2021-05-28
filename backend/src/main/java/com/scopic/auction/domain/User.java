package com.scopic.auction.domain;

import com.scopic.auction.dto.UserDto;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class User {

    private String username;

    public User() {
    }

    public User(String username) {
        this.username = username;
    }

    public UserDto toDto() {
        return new UserDto(username);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
