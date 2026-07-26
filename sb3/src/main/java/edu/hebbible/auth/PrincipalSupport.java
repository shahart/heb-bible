package edu.hebbible.auth;

import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Objects;

public final class PrincipalSupport {

    private PrincipalSupport() {
    }

    public static String userId(Object principal) {
        if (principal instanceof AuthenticatedUser user) {
            return user.email();
        }
        if (principal instanceof OAuth2User user) {
            return Objects.requireNonNullElse(user.getAttribute("email"), user.getName());
        }
        throw new IllegalArgumentException("Unsupported principal type");
    }

    public static AuthenticatedUser authenticatedUser(Object principal) {
        if (principal instanceof AuthenticatedUser user) {
            return user;
        }
        if (principal instanceof OAuth2User user) {
            String email = user.getAttribute("email");
            String name = user.getAttribute("name");
            String id = email == null ? user.getName() : email;
            return new AuthenticatedUser(id, name == null ? id : name, email);
        }
        throw new IllegalArgumentException("Unsupported principal type");
    }
}
