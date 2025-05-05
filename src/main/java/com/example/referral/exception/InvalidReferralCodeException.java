package com.example.referral.exception;

/**
 * Exception thrown when a referral code is invalid.
 */
public class InvalidReferralCodeException extends RuntimeException {
    public InvalidReferralCodeException(String message) {
        super(message);
    }
}
