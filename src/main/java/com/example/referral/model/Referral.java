package com.example.referral.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a referral relationship between a referrer and a referred user.
 */
@Data
@Entity
@Getter
@Setter
public class Referral {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReferrerId() {
        return referrerId;
    }

    public void setReferrerId(String referrerId) {
        this.referrerId = referrerId;
    }

    public String getReferredId() {
        return referredId;
    }

    public void setReferredId(String referredId) {
        this.referredId = referredId;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    private String referrerId;  // User who referred
    private String referredId;  // User who was referred
    private boolean successful; // True if referred user completed their profile
}