package com.yuriykoziy.issueTracker.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yuriykoziy.issueTracker.constants.ResponseConstants;
import com.yuriykoziy.issueTracker.dto.UserProfileDto;
import com.yuriykoziy.issueTracker.models.UserFilterCriteria;
import com.yuriykoziy.issueTracker.services.UserProfileService;

import lombok.AllArgsConstructor;

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
    public boolean updateProfile(@RequestBody UserProfileDto request, @RequestParam Long id) {
        return userProfileService.updateProfile(request, id);
    }

    @GetMapping("/users")
    public Map<String, Object> getAllUsers(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<UserProfileDto> userPage = userProfileService.getAllUsers(page, size);
        List<UserProfileDto> users = userPage.getContent();
        Map<String, Object> response = new HashMap<>();
        response.put(ResponseConstants.USERS, users);
        response.put(ResponseConstants.NUMBER, userPage.getNumber());
        response.put(ResponseConstants.TOTAL_ELEMENTS, userPage.getTotalElements());
        response.put(ResponseConstants.TOTAL_PAGES, userPage.getTotalPages());
        response.put(ResponseConstants.SIZE, userPage.getSize());
        return response;
    }

    @GetMapping(value = "/filter")
    public Map<String, Object> filterBySpecs(
            UserFilterCriteria criteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<UserProfileDto> userPage = userProfileService.findAllCriteria(criteria.getEnabled(), criteria.getLocked(),
                page, size);
        List<UserProfileDto> users = userPage.getContent();
        Map<String, Object> response = new HashMap<>();
        response.put(ResponseConstants.USERS, users);
        response.put(ResponseConstants.NUMBER, userPage.getNumber());
        response.put(ResponseConstants.TOTAL_ELEMENTS, userPage.getTotalElements());
        response.put(ResponseConstants.TOTAL_PAGES, userPage.getTotalPages());
        response.put(ResponseConstants.SIZE, userPage.getSize());
        return response;
    }

    @PostMapping("/ban")
    public boolean banUser(@RequestBody UserProfileDto request) {
        return userProfileService.banUser(request);
    }

}
