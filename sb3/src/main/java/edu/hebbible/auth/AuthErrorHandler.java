package edu.hebbible.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestControllerAdvice(assignableTypes = AuthController.class)
public class AuthErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(AuthErrorHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, String>> validationError(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Invalid signup details");
        log.warn("validationError: " + message);
        return ResponseEntity.badRequest().body(Map.of("message", message));
    }

    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<Map<String, String>> authenticationError(ResponseStatusException exception) {
        String message = exception.getReason() == null ? "Authentication failed" : exception.getReason();
        log.warn("authenticationError: " + message);
        return ResponseEntity.status(exception.getStatusCode()).body(Map.of("message", message));
    }

    @ExceptionHandler(UserManagementUnavailableException.class)
    ResponseEntity<Map<String, String>> userManagementUnavailable(
            UserManagementUnavailableException exception) {
        log.error("userManagementUnavailable", exception);
        return ResponseEntity.status(503)
                .body(Map.of("message", "User management is temporarily unavailable"));
    }
}
