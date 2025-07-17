package com.E2DShare.filemngt.controller;


import com.E2DShare.filemngt.model.UserRegistrationRequest;
import com.E2DShare.filemngt.service.UserSignupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserSignupService signupService;

    public UserController(UserSignupService signupService) {
        this.signupService = signupService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestParam String userId,
            @RequestParam String username,
            @RequestParam String password) {
        try {
            signupService.registerUser(userId, username, password);
            return ResponseEntity.ok("✅ Registered user: " + userId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Registration failed: " + e.getMessage());
        }
    }




}

