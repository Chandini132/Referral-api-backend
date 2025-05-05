package com.example.referral.repository;

import com.example.referral.model.Referral;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for referral entities.
 */
public interface ReferralRepository extends JpaRepository<Referral, Long> {
    List<Referral> findByReferrerId(String referrerId);
    List<Referral> findByReferredId(String referredId);
}