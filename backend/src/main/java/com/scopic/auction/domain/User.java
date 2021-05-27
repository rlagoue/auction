package com.scopic.auction.domain;

import com.scopic.auction.dto.UserDto;

import javax.persistence.Embeddable;

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
}
