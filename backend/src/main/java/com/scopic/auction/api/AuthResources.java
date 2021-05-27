package com.scopic.auction.api;

import com.scopic.auction.dto.UserDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class AuthResources {

    private static final List<String> UserRepository = List.of(
            "admin", "user1", "user2"
    );

    @PostMapping("/authenticate")
    @ResponseBody
    public String authenticate(@RequestBody UserDto user) {
        return UserRepository.contains(user.username) ? "token" : "";
    }
}
