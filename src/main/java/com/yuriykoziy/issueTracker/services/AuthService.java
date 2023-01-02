package com.yuriykoziy.issueTracker.services;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.yuriykoziy.issueTracker.dto.UserDto;
import com.yuriykoziy.issueTracker.models.UserProfile;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {

   public UserDto getAuthUser(Authentication auth) {
      UserProfile authProfile = (UserProfile) auth.getPrincipal();
      UserDto userDto = new UserDto();
      userDto.setId(authProfile.getId());
      userDto.setUserRole(authProfile.getUserRole());
      return userDto;
   }
}
