package com.energydashboard.controller;

import com.energydashboard.dto.AuthRequest;
import com.energydashboard.dto.AuthResponse;
import com.energydashboard.model.User;
import com.energydashboard.repository.UserRepository;
import com.energydashboard.security.JwtUtility;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtility jwtUtility;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, JwtUtility jwtUtility, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtility = jwtUtility;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        User user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getName());
        userRepository.save(user);

        String token = jwtUtility.generateToken(user.getEmail());
        return ResponseEntity.ok(new AuthResponse(token, user.getName()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .map(user -> {
                    String token = jwtUtility.generateToken(user.getEmail());
                    return ResponseEntity.ok((Object) new AuthResponse(token, user.getName()));
                })
                .orElse(ResponseEntity.badRequest().body("Invalid email or password"));
    }
}