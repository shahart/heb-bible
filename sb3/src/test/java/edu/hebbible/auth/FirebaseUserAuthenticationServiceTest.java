package edu.hebbible.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class FirebaseUserAuthenticationServiceTest {

    private MockRestServiceServer server;
    private FirebaseUserAuthenticationService service;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        service = new FirebaseUserAuthenticationService(builder, "test-api-key");
    }

    @Test
    void signupCreatesFirebaseUserAndNormalizesEmail() {
        server.expect(requestTo(
                        "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=test-api-key"))
                .andExpect(method(POST))
                .andExpect(content().json("""
                        {
                          "email": "person@example.com",
                          "password": "correct horse",
                          "returnSecureToken": true
                        }
                        """))
                .andRespond(withSuccess("""
                        {"localId":"firebase-uid","email":"person@example.com"}
                        """, MediaType.APPLICATION_JSON));

        ManagedUser user = service.signup(" Person@Example.com ", "correct horse");

        assertEquals("firebase:firebase-uid", user.id());
        assertEquals("person@example.com", user.email());
        server.verify();
    }

    @Test
    void signupMapsExistingEmailToDuplicateKey() {
        server.expect(requestTo(
                        "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=test-api-key"))
                .andRespond(withBadRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("""
                                {"error":{"code":400,"message":"EMAIL_EXISTS"}}
                                """));

        assertThrows(DuplicateKeyException.class,
                () -> service.signup("person@example.com", "correct horse"));
        server.verify();
    }

    @Test
    void loginReturnsFirebaseUser() {
        server.expect(requestTo(
                        "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=test-api-key"))
                .andExpect(method(POST))
                .andRespond(withSuccess("""
                        {"localId":"firebase-uid","email":"person@example.com"}
                        """, MediaType.APPLICATION_JSON));

        Optional<ManagedUser> user = service.authenticate(
                "person@example.com", "correct horse");

        assertEquals("firebase:firebase-uid", user.orElseThrow().id());
        server.verify();
    }

    @Test
    void loginMapsInvalidFirebaseCredentialsToEmpty() {
        server.expect(requestTo(
                        "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=test-api-key"))
                .andRespond(withBadRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("""
                                {"error":{"code":400,"message":"INVALID_LOGIN_CREDENTIALS"}}
                                """));

        assertEquals(Optional.empty(),
                service.authenticate("person@example.com", "wrong horse"));
        server.verify();
    }

    @Test
    void requiresApiKeyWhenFirebaseIsSelected() {
        RestClient.Builder builder = RestClient.builder();

        assertThrows(IllegalStateException.class,
                () -> new FirebaseUserAuthenticationService(builder, " "));
    }
}
