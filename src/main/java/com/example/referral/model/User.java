package com.example.referral.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

/**
 * Represents a user with a referral code, profile completion status, password, and role.
 */
@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    private String userId ;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isProfileCompleted() {
        return profileCompleted;
    }

    public void setProfileCompleted(boolean profileCompleted) {
        this.profileCompleted = profileCompleted;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String referralCode;  // Unique referral code for this user
    private boolean profileCompleted;
    private String password;     // Hashed password
    private String role;         // e.g., "USER"
}