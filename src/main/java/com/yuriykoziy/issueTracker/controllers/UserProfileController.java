package com.yuriykoziy.issueTracker.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
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
@RequestMapping(path = "api/v1/profiles")
@AllArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/{id}")
    public UserProfileDto findUserProfileById(@PathVariable Long id) {
        return userProfileService.getUserProfileById(id);
    }

    @PutMapping("/{id}")
    public void updateProfile(@PathVariable Long id, @RequestBody UserProfileDto request) {
        userProfileService.updateProfile(id, request);
    }

    @GetMapping()
    public Map<String, Object> getAllBySpecs(
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

    @PutMapping("/ban")
    public void banUser(@RequestBody UserProfileDto user) {
        userProfileService.banUser(user);
    }

    @PutMapping("/unlock")
    public void unlockUser(@RequestBody UserProfileDto user) {
        userProfileService.unlockUser(user);
    }

}
