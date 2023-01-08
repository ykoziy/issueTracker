package com.yuriykoziy.issueTracker.services;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.yuriykoziy.issueTracker.dto.UserDto;
import com.yuriykoziy.issueTracker.models.UserProfile;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {

   private final ModelMapper modelMapper;

   public UserDto getAuthUser(Authentication auth) {
      UserProfile authProfile = (UserProfile) auth.getPrincipal();
      return modelMapper.map(authProfile, UserDto.class);
   }
}
