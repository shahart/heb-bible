package edu.hebbible.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void signupReturnsJwtAndBearerTokenAuthenticatesUserEndpoint() throws Exception {
        String email = "jwt-" + UUID.randomUUID() + "@example.com";
        String body = """
                {"email":"%s","password":"correct horse"}
                """.formatted(email);

        String token = mvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.token").value(matchesPattern("[^.]+\\.[^.]+\\.[^.]+")))
                .andReturn()
                .getResponse()
                .getContentAsString()
                .replaceFirst(".*\"token\":\"([^\"]+)\".*", "$1");

        mvc.perform(get("/user")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void loginRejectsInvalidPassword() throws Exception {
        String email = "jwt-" + UUID.randomUUID() + "@example.com";
        mvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"correct horse"}
                                """.formatted(email)))
                .andExpect(status().isCreated());

        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"wrong horse"}
                                """.formatted(email)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginBlocksAfterThreeFailedAttemptsWithinFiveMinutes() throws Exception {
        String email = "rate-limit-" + UUID.randomUUID() + "@example.com";
        mvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"correct horse"}
                                """.formatted(email)))
                .andExpect(status().isCreated());

        String invalidLogin = """
                {"email":"%s","password":"wrong horse"}
                """.formatted(email);
        for (int attempt = 0; attempt < 3; attempt++) {
            mvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidLogin))
                    .andExpect(status().isUnauthorized());
        }

        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidLogin))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.message")
                        .value("Too many login attempts. Try again later"));
    }

    @Test
    void signupExplainsPasswordLengthValidationFailure() throws Exception {
        mvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"short-password@example.com","password":"short"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Password must be between 8 and 100 characters"));
    }
}
