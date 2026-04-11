package pl.banzaijiujitsu.backend.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.banzaijiujitsu.backend.exception.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<String> handleInvalidEmailException(InvalidEmailException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage() != null ? ex.getMessage() : "INVALID_EMAIL");
    }

    @ExceptionHandler(InvalidLocationException.class)
    public ResponseEntity<String> handleInvalidLocationException(InvalidLocationException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage() != null ?  ex.getMessage() : "INVALID_LOCATION");
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<String> handleInvalidPasswordException(InvalidPasswordException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage() != null ? ex.getMessage() : "INVALID_PASSWORD");
    }

    @ExceptionHandler(InvalidPaymentException.class)
    public ResponseEntity<String> handleInvalidPaymentException(InvalidPaymentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage() != null ? ex.getMessage() : "INVALID_PAYMENT");
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<String> handleInvalidRefreshTokenException(InvalidRefreshTokenException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage() != null ? ex.getMessage() : "INVALID_REFRESH_TOKEN");
    }

    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<String> handleInvalidRoleException(InvalidRoleException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage() != null ? ex.getMessage() : "INVALID_ROLE");
    }

    @ExceptionHandler(InvalidStatusException.class)
    public ResponseEntity<String> handleInvalidStatusException(InvalidStatusException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage() != null ? ex.getMessage() : "INVALID_STATUS");
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<String> handleInvalidTokenException(InvalidTokenException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage() != null ? ex.getMessage() : "INVALID_TOKEN");
    }

    @ExceptionHandler(InvalidUuidException.class)
    public ResponseEntity<String> handleInvalidUuidException(InvalidUuidException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage() != null ? ex.getMessage() : "INVALID_UUID");
    }

    @ExceptionHandler(UserNotActiveException.class)
    public ResponseEntity<String> handleUserNotActiveException(UserNotActiveException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage() != null ? ex.getMessage() : "USER_NOT_ACTIVE");
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage() != null ? ex.getMessage() : "USERNAME_NOT_FOUND");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage() != null ? ex.getMessage() : "BAD_CREDENTIALS");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage() != null ? ex.getMessage() : "NO_ACCESS");
    }
}
