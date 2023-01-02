package com.yuriykoziy.issueTracker.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yuriykoziy.issueTracker.dto.UserDto;
import com.yuriykoziy.issueTracker.models.UserProfile;
import com.yuriykoziy.issueTracker.services.AuthService;

import lombok.AllArgsConstructor;

@RestController
@CrossOrigin
@RequestMapping(path = "api/v1/basicauth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping
    public UserDto basicauth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return authService.getAuthUser(auth);
    }
}
