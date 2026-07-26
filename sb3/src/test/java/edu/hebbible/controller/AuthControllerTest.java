package edu.hebbible.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private StringRedisTemplate redis;

    @BeforeEach
    void configureLoginAttemptStorage() {
        Map<String, AtomicInteger> attempts = new ConcurrentHashMap<>();
        Mockito.when(redis.execute(any(RedisScript.class), anyList(),
                        anyString(), anyString(), anyString()))
                .thenAnswer(invocation -> {
                    List<String> keys = invocation.getArgument(1);
                    int count = attempts.computeIfAbsent(keys.getFirst(), ignored -> new AtomicInteger())
                            .incrementAndGet();
                    return count <= 3 ? 1L : 0L;
                });
        Mockito.when(redis.delete(anyString()))
                .thenAnswer(invocation -> attempts.remove(invocation.getArgument(0)) != null);
    }

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
