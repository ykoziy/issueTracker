package com.yuriykoziy.issueTracker.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yuriykoziy.issueTracker.services.AuthService;
import com.yuriykoziy.issueTracker.controllers.auth.AuthenticationResponse;
import com.yuriykoziy.issueTracker.controllers.auth.AuthenticationRequest;
import com.yuriykoziy.issueTracker.dto.RegistrationDto;

import lombok.AllArgsConstructor;

@RestController
@CrossOrigin
@RequestMapping(path = "api/v1/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public boolean register(@RequestBody RegistrationDto request) {
        return authService.register(request);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok("Email verification successful.");
    }
}
