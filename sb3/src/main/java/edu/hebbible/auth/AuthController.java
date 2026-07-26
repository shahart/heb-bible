package edu.hebbible.auth;

import edu.hebbible.controller.UserController;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@RestController
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final LocalUserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(LocalUserRepository users, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/auth/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse signup(@Valid @RequestBody AuthRequest request) {
        try {
            log.info("signup: " + request.email());
            LocalUser user = users.create(request.email(), passwordEncoder.encode(request.password()));
            return response(user);
        } catch (DuplicateKeyException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered", e);
        }
    }

    @PostMapping("/auth/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        log.info("login: " + request.email());
        LocalUser user = users.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        if (!passwordEncoder.matches(request.password(), user.passwordHash())) {
            log.warn("Invalid email or password: " + request.email());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        log.info("login successful: " + request.email());
        return response(user);
    }

    private AuthResponse response(LocalUser user) {
        String name = user.email().substring(0, user.email().indexOf('@')).toLowerCase(Locale.ROOT);
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(
                "local:" + user.id(),
                name,
                user.email());
        return new AuthResponse(jwtService.createToken(authenticatedUser), "Bearer", user.email(), name);
    }
}
