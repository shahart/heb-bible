package edu.hebbible.auth;

public record LocalUser(long id, String email, String passwordHash) {
}
