package com.support.controller;

import com.support.dto.UserRegisterRequest;
import com.support.dto.LoginRequest;
import com.support.entity.User;
import com.support.entity.UserRole;
import com.support.repository.UserRepository;
import com.support.security.JwtUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterRequest req) {

        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("error", "Email already exists"));
        }

        User u = new User();
        u.setEmail(req.getEmail());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setName(req.getName());
        u.setRole(req.getRole() == null ? UserRole.EMPLOYEE : req.getRole());

        userRepo.save(u);

        return ResponseEntity.ok(Map.of("message", "Registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        System.out.println("LOGIN EMAIL = " + req.getEmail());
        System.out.println("RAW INPUT = [" + req.getPassword() + "]");


        User user = userRepo.findByEmail(req.getEmail()).orElse(null);
        System.out.println("DB HASH   = [" + user.getPassword() + "]");
        System.out.println("MATCH?    = " +
                passwordEncoder.matches(req.getPassword(), user.getPassword()));
        if (user == null) return ResponseEntity.status(404).body(Map.of("error", "User not found"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword()))
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));

        String token = jwtUtil.generateToken(user);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "role", user.getRole()
        ));
    }
    @GetMapping("/encode/{raw}")
    public String encode(@PathVariable String raw) {
        return passwordEncoder.encode(raw);
    }

}
