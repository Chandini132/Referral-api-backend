package com.example.referral.controller;

import com.example.referral.model.Referral;
import com.example.referral.model.User;
import com.example.referral.repository.ReferralRepository;
import com.example.referral.security.JwtUtil;
import com.example.referral.service.ReferralService;
import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

/**
 * REST controller for referral-related endpoints.
 */
@RestController
@RequestMapping("/api")
public class ReferralController {
    private final ReferralService referralService;
    private final ReferralRepository referralRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public ReferralController(ReferralService referralService, ReferralRepository referralRepository, JwtUtil jwtUtil) {
        this.referralService = referralService;
        this.referralRepository = referralRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Authenticates a user and returns a JWT token.
     */
    @PostMapping("/auth/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        String password = request.get("password");
        String token = referralService.login(userId, password, jwtUtil);
        return ResponseEntity.ok(Map.of("token", token));
    }

    /**
     * Signs up a new user, optionally with a referral code.
     */
    @PostMapping("/users/signup")
    public ResponseEntity<User> signup(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        String password = request.get("password");
        String referralCode = request.get("referralCode");
        User user = referralService.signup(userId, password, referralCode);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    /**
     * Completes a user's profile and updates referral status.
     */
    @PostMapping("/users/profile")
    public ResponseEntity<Void> completeProfile(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        referralService.completeProfile(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves a user's referrals.
     */
    @GetMapping("/users/{userId}/referrals")
    public ResponseEntity<List<Referral>> getReferrals(@PathVariable String userId) {
        List<Referral> referrals = referralService.getReferrals(userId);
        return ResponseEntity.ok(referrals);
    }

    /**
     * Generates a CSV report of all users and their referrals.
     */
    @GetMapping("/referrals/report")
    public ResponseEntity<ByteArrayResource> generateReferralReport() throws IOException {
        List<Referral> referrals = referralRepository.findAll();
        StringWriter stringWriter = new StringWriter();
        try (CSVWriter csvWriter = new CSVWriter(stringWriter)) {
            String[] header = {"Referrer ID", "Referred ID", "Successful"};
            csvWriter.writeNext(header);
            for (Referral referral : referrals) {
                csvWriter.writeNext(new String[]{
                        referral.getReferrerId(),
                        referral.getReferredId(),
                        String.valueOf(referral.isSuccessful())
                });
            }
        }
        ByteArrayResource resource = new ByteArrayResource(stringWriter.toString().getBytes());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=referral_report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }
}