package edu.hebbible.auth;

import java.util.Optional;

interface UserAuthenticationService {

    ManagedUser signup(String email, String password);

    Optional<ManagedUser> authenticate(String email, String password);
}
