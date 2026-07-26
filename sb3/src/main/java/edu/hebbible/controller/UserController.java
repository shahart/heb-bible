package edu.hebbible.controller;

import edu.hebbible.auth.AuthenticatedUser;
import edu.hebbible.auth.PrincipalSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping("user")
    public Map<String, Object> user(@AuthenticationPrincipal Object principal) {
        AuthenticatedUser user = PrincipalSupport.authenticatedUser(principal);
        Map<String, Object> result = new HashMap<>();
        result.put("name", user.name());
        result.put("email", user.email());

        log.info("user: " + user.email());

        return result;
    }
}
