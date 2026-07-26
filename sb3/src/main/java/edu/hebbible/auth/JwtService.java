package edu.hebbible.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class JwtService {

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Clock clock;
    private final byte[] secret;
    private final long expiresInSeconds;

    @Autowired
    public JwtService(@Value("${hebbible.jwt.secret:change-this-development-secret-at-least-32-bytes}") String secret,
                      @Value("${hebbible.jwt.expires-in-seconds:86400}") long expiresInSeconds) {
        this(Clock.systemUTC(), secret, expiresInSeconds);
    }

    JwtService(Clock clock, String secret, long expiresInSeconds) {
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 bytes");
        }
        this.clock = clock;
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.expiresInSeconds = expiresInSeconds;
    }

    public String createToken(AuthenticatedUser user) {
        Instant now = Instant.now(clock);
        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("sub", user.id());
        claims.put("name", user.name());
        claims.put("email", user.email());
        claims.put("iat", now.getEpochSecond());
        claims.put("exp", now.plusSeconds(expiresInSeconds).getEpochSecond());

        String unsignedToken = encodeJson(header) + "." + encodeJson(claims);
        return unsignedToken + "." + sign(unsignedToken);
    }

    public Optional<AuthenticatedUser> parseToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return Optional.empty();
            }
            String unsignedToken = parts[0] + "." + parts[1];
            if (!sign(unsignedToken).equals(parts[2])) {
                return Optional.empty();
            }

            Map<String, Object> claims = objectMapper.readValue(
                    Base64.getUrlDecoder().decode(parts[1]), MAP_TYPE);
            if (longClaim(claims, "exp") <= Instant.now(clock).getEpochSecond()) {
                return Optional.empty();
            }

            String id = stringClaim(claims, "sub");
            String email = stringClaim(claims, "email");
            String name = stringClaim(claims, "name");
            if (id == null || email == null) {
                return Optional.empty();
            }
            return Optional.of(new AuthenticatedUser(id, name == null ? email : name, email));
        } catch (RuntimeException | java.io.IOException e) {
            return Optional.empty();
        }
    }

    private String encodeJson(Map<String, Object> value) {
        try {
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(objectMapper.writeValueAsBytes(value));
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Could not encode JWT", e);
        }
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(new SecretKeySpec(secret, HMAC_SHA256));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (java.security.GeneralSecurityException e) {
            throw new IllegalStateException("Could not sign JWT", e);
        }
    }

    private String stringClaim(Map<String, Object> claims, String name) {
        Object value = claims.get(name);
        return value instanceof String string ? string : null;
    }

    private long longClaim(Map<String, Object> claims, String name) {
        Object value = claims.get(name);
        return value instanceof Number number ? number.longValue() : 0;
    }
}
