package com.example.referral.exception;

/**
 * Exception thrown when authentication fails (e.g., invalid password).
 */
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}