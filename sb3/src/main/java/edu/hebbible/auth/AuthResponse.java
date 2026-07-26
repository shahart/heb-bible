package edu.hebbible.auth;

public record AuthResponse(String token, String tokenType, String email, String name) {
}
