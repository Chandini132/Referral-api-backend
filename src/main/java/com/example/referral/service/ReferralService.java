package com.example.referral.service;

import com.example.referral.exception.AuthenticationException;
import com.example.referral.exception.InvalidReferralCodeException;
import com.example.referral.exception.UserAlreadyExistsException;
import com.example.referral.exception.UserNotFoundException;
import com.example.referral.model.Referral;
import com.example.referral.model.User;
import com.example.referral.repository.ReferralRepository;
import com.example.referral.repository.UserRepository;
import com.example.referral.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service for handling user signups, referrals, and profile completion.
 */
@Service
public class ReferralService {
    private final UserRepository userRepository;
    private final ReferralRepository referralRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ReferralService(UserRepository userRepository, ReferralRepository referralRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.referralRepository = referralRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Signs up a new user, generates a referral code, and links to a referrer if provided.
     */
    public User signup(String userId, String password, String referralCode) {
        // Check if user already exists
        if (userRepository.existsById(userId)) {
            throw new UserAlreadyExistsException("User already exists with ID: " + userId);
        }

        // Create new user
        User user = new User();
        user.setUserId(userId);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");
        user.setReferralCode(generateReferralCode());
        user.setProfileCompleted(false);
        userRepository.save(user);

        // Link referral if referral code is provided
        if (referralCode != null && !referralCode.isEmpty()) {
            User referrer = userRepository.findByReferralCode(referralCode)
                    .orElseThrow(() -> new InvalidReferralCodeException("Invalid referral code: " + referralCode));
            Referral referral = new Referral();
            referral.setReferrerId(referrer.getUserId());
            referral.setReferredId(userId);
            referral.setSuccessful(false);
            referralRepository.save(referral);
        }

        return user;
    }

    /**
     * Authenticates a user and returns a JWT token.
     */
    public String login(String userId, String password, JwtUtil jwtUtil) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthenticationException("Invalid password for user ID: " + userId);
        }
        return jwtUtil.generateToken(userId, user.getRole());
    }

    /**
     * Completes a user's profile and marks any associated referrals as successful.
     */
    public void completeProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        user.setProfileCompleted(true);
        userRepository.save(user);

        // Mark referrals as successful
        List<Referral> referrals = referralRepository.findByReferredId(userId);
        for (Referral referral : referrals) {
            referral.setSuccessful(true);
            referralRepository.save(referral);
        }
    }

    /**
     * Retrieves a user's referrals.
     */
    public List<Referral> getReferrals(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
        return referralRepository.findByReferrerId(userId);
    }

    /**
     * Generates a unique referral code.
     */
    private String generateReferralCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().substring(0, 8);
        } while (userRepository.findByReferralCode(code).isPresent());
        return code;
    }
}