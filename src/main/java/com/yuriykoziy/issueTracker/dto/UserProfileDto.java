package com.yuriykoziy.issueTracker.dto;

import com.yuriykoziy.issueTracker.enums.UserRole;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
public class UserProfileDto {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;
    private Boolean locked;
}
