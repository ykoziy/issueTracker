package com.yuriykoziy.issueTracker.dto;

import com.yuriykoziy.issueTracker.enums.UserRole;

import lombok.Data;

@Data
public class UserDto {
   private Long id;
   private UserRole userRole;
}
