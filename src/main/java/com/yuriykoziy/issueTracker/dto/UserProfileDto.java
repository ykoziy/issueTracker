package com.yuriykoziy.issueTracker.dto;

import com.yuriykoziy.issueTracker.enums.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

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
    private Boolean enabled;
    private LocalDateTime createdOn;
    private LocalDateTime lockedOn;
    private LocalDateTime updatedOn;
    private LocalDateTime disabledOn;
}
