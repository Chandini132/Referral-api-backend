package com.example.referral.service;



import com.example.referral.exception.UserAlreadyExistsException;
import com.example.referral.exception.UserNotFoundException;
import com.example.referral.model.Referral;
import com.example.referral.model.User;
import com.example.referral.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ReferralService.
 */
@SpringBootTest
public class ReferralServiceTest {
    @Autowired
    private ReferralService referralService;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void testSignupAndReferral() {
        // Sign up user A
        User userA = referralService.signup("userA", "passwordA", null);
        assertEquals("userA", userA.getUserId());
        assertNotNull(userA.getReferralCode());

        // Sign up user B with user A's referral code
        User userB = referralService.signup("userB", "passwordB", userA.getReferralCode());
        assertEquals("userB", userB.getUserId());
        assertNotNull(userB.getReferralCode());

        // Authenticate user A
        String token = referralService.login("userA", "passwordA", jwtUtil);
        assertNotNull(token);

        // Check referrals for user A
        List<Referral> referrals = referralService.getReferrals("userA");
        assertEquals(1, referrals.size());
        assertEquals("userB", referrals.get(0).getReferredId());
        assertFalse(referrals.get(0).isSuccessful());

        // Complete user B's profile
        referralService.completeProfile("userB");

        // Verify referral is now successful
        referrals = referralService.getReferrals("userA");
        assertTrue(referrals.get(0).isSuccessful());
    }

    @Test
    public void testSignupUserAlreadyExists() {
        // Sign up user A
        referralService.signup("userA", "passwordA", null);

        // Attempt to sign up with the same userId
        assertThrows(UserAlreadyExistsException.class, () -> {
            referralService.signup("userA", "passwordB", null);
        });
    }

    @Test
    public void testLoginUserNotFound() {
        assertThrows(UserNotFoundException.class, () -> {
            referralService.login("nonexistent", "password", jwtUtil);
        });
    }
}
