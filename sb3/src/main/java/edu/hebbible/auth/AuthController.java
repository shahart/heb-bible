package edu.hebbible.auth;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@RestController
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserAuthenticationService userAuthentication;
    private final JwtService jwtService;
    private final LoginAttemptLimiter loginAttemptLimiter;

    public AuthController(UserAuthenticationService userAuthentication, JwtService jwtService,
                          LoginAttemptLimiter loginAttemptLimiter) {
        this.userAuthentication = userAuthentication;
        this.jwtService = jwtService;
        this.loginAttemptLimiter = loginAttemptLimiter;
    }

    @PostMapping("/auth/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse signup(@Valid @RequestBody AuthRequest request) {
        try {
            log.info("signup: " + request.email());
            ManagedUser user = userAuthentication.signup(request.email(), request.password());
            return response(user);
        } catch (DuplicateKeyException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered", e);
        }
    }

    @PostMapping("/auth/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        log.info("login: " + request.email());
        if (!loginAttemptLimiter.tryAcquire(request.email())) {
            log.warn("Login temporarily blocked: " + request.email());
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Too many login attempts. Try again later");
        }

        ManagedUser user = userAuthentication.authenticate(request.email(), request.password())
                .orElse(null);
        if (user == null) {
            log.warn("Invalid email or password: " + request.email());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        loginAttemptLimiter.recordSuccess(request.email());
        log.info("login successful: " + request.email());
        return response(user);
    }

    private AuthResponse response(ManagedUser user) {
        String name = user.email().substring(0, user.email().indexOf('@')).toLowerCase(Locale.ROOT);
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(
                user.id(),
                name,
                user.email());
        return new AuthResponse(jwtService.createToken(authenticatedUser), "Bearer", user.email(), name);
    }
}
