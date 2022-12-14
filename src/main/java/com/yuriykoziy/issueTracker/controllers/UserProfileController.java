package com.yuriykoziy.issueTracker.controllers;

import com.yuriykoziy.issueTracker.dto.UserProfileDto;
import com.yuriykoziy.issueTracker.services.UserProfileService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping(path = "api/v1/profile")
@AllArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping(params = "id")
    public UserProfileDto findUserProfileById(@RequestParam Long id) {
        return userProfileService.getUserProfileById(id);
    }

    @PostMapping
    public boolean updateProfile(@RequestBody UserProfileDto request) {
        return userProfileService.updateProfile(request);
    }

}
