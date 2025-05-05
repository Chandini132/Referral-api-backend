package com.example.referral.exception;



/**
 * Exception thrown when a user attempts to sign up with an existing user ID.
 */
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}