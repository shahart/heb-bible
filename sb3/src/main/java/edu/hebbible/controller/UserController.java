package edu.hebbible.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping("user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User user) {
        Map<String, Object> result = new HashMap<>();
        result.put("name", user.getAttribute("name"));
        result.put("email", user.getAttribute("email"));

        log.info("user: " + user.getAttribute("email"));

        return result;
    }
}
