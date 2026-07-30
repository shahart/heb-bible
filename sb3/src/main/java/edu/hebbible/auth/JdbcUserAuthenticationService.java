package edu.hebbible.auth;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@ConditionalOnProperty(
        name = "hebbible.auth.user-management.provider",
        havingValue = "jdbc",
        matchIfMissing = true)
class JdbcUserAuthenticationService implements UserAuthenticationService {

    private final LocalUserRepository users;
    private final PasswordEncoder passwordEncoder;

    JdbcUserAuthenticationService(LocalUserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ManagedUser signup(String email, String password) {
        LocalUser user = users.create(email, passwordEncoder.encode(password));
        return managedUser(user);
    }

    @Override
    public Optional<ManagedUser> authenticate(String email, String password) {
        return users.findByEmail(email)
                .filter(user -> passwordEncoder.matches(password, user.passwordHash()))
                .map(this::managedUser);
    }

    private ManagedUser managedUser(LocalUser user) {
        return new ManagedUser("local:" + user.id(), user.email());
    }
}
