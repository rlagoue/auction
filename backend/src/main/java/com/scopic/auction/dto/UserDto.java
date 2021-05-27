package com.scopic.auction.dto;

public class UserDto {
    public String username;
    public String password;

    public UserDto() {
    }

    public UserDto(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
