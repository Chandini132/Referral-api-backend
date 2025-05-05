package com.example.referral.repository;

import com.example.referral.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for user entities.
 */
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByReferralCode(String referralCode);
}