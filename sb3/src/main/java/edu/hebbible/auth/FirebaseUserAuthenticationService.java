package edu.hebbible.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Service
@ConditionalOnProperty(
        name = "hebbible.auth.user-management.provider",
        havingValue = "firebase")
class FirebaseUserAuthenticationService implements UserAuthenticationService {

    private static final Set<String> INVALID_CREDENTIAL_ERRORS = Set.of(
            "EMAIL_NOT_FOUND",
            "INVALID_EMAIL",
            "INVALID_LOGIN_CREDENTIALS",
            "INVALID_PASSWORD",
            "USER_DISABLED");

    private final RestClient restClient;
    private final String apiKey;

    @Autowired
    FirebaseUserAuthenticationService(
            @Value("${hebbible.auth.firebase.api-key:}") String apiKey) {
        this(RestClient.builder(), apiKey);
    }

    FirebaseUserAuthenticationService(RestClient.Builder restClientBuilder, String apiKey) {
        if (apiKey.isBlank()) {
            throw new IllegalStateException(
                    "hebbible.auth.firebase.api-key must be configured when Firebase user management is enabled");
        }
        this.restClient = restClientBuilder
                .baseUrl("https://identitytoolkit.googleapis.com")
                .build();
        this.apiKey = apiKey;
    }

    @Override
    public ManagedUser signup(String email, String password) {
        try {
            return request("/v1/accounts:signUp", email, password);
        } catch (RestClientResponseException exception) {
            if ("EMAIL_EXISTS".equals(firebaseErrorCode(exception))) {
                throw new DuplicateKeyException("Email is already registered", exception);
            }
            throw unavailable(exception);
        } catch (RestClientException exception) {
            throw unavailable(exception);
        }
    }

    @Override
    public Optional<ManagedUser> authenticate(String email, String password) {
        try {
            return Optional.of(request("/v1/accounts:signInWithPassword", email, password));
        } catch (RestClientResponseException exception) {
            if (INVALID_CREDENTIAL_ERRORS.contains(firebaseErrorCode(exception))) {
                return Optional.empty();
            }
            throw unavailable(exception);
        } catch (RestClientException exception) {
            throw unavailable(exception);
        }
    }

    private ManagedUser request(String path, String email, String password) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        FirebaseAuthResponse response = restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParam("key", apiKey)
                        .build())
                .body(new FirebaseAuthRequest(normalizedEmail, password, true))
                .retrieve()
                .body(FirebaseAuthResponse.class);
        if (response == null || response.localId() == null || response.localId().isBlank()) {
            throw new UserManagementUnavailableException(
                    "Firebase Authentication returned an incomplete response", null);
        }
        String responseEmail = response.email() == null ? normalizedEmail : response.email();
        return new ManagedUser("firebase:" + response.localId(), responseEmail);
    }

    private String firebaseErrorCode(RestClientResponseException exception) {
        try {
            FirebaseErrorEnvelope response = exception.getResponseBodyAs(FirebaseErrorEnvelope.class);
            if (response != null && response.error() != null && response.error().message() != null) {
                return response.error().message().split(" : ", 2)[0];
            }
        } catch (RuntimeException ignored) {
            // A malformed upstream error is handled as a provider outage.
        }
        return "";
    }

    private UserManagementUnavailableException unavailable(RestClientException cause) {
        return new UserManagementUnavailableException(
                "Firebase Authentication is temporarily unavailable", cause);
    }

    private record FirebaseAuthRequest(String email, String password, boolean returnSecureToken) {
    }

    private record FirebaseAuthResponse(String localId, String email) {
    }

    private record FirebaseErrorEnvelope(FirebaseError error) {
    }

    private record FirebaseError(String message) {
    }
}
