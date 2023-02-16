package com.yuriykoziy.issueTracker.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.yuriykoziy.issueTracker.constants.ErrorMessages;
import com.yuriykoziy.issueTracker.controllers.auth.AuthenticationRequest;
import com.yuriykoziy.issueTracker.controllers.auth.AuthenticationResponse;
import com.yuriykoziy.issueTracker.dto.RegistrationDto;
import com.yuriykoziy.issueTracker.enums.UserRole;
import com.yuriykoziy.issueTracker.models.UserProfile;
import com.yuriykoziy.issueTracker.repositories.UserProfileRepository;
import com.yuriykoziy.issueTracker.security.jwt.JwtService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {
   private final EmailValidator emailValidator;
   private final JwtService jwtService;
   private final AuthenticationManager authenticationManager;
   private final UserProfileRepository userProfileRepository;
   private final PasswordEncoder passwordEncoder;

   public boolean register(RegistrationDto request) {
      boolean isValidEmail = emailValidator.test(request.getEmail());
      if (!isValidEmail) {
         throw new IllegalStateException(ErrorMessages.INVALID_EMAIL);
      }

      UserProfile userProfile = new UserProfile(
            request.getFirstName(),
            request.getLastName(),
            request.getEmail(),
            request.getUsername(),
            passwordEncoder.encode(request.getPassword()),
            UserRole.USER);

      boolean userEmailExists = userProfileRepository
            .findByEmail(userProfile.getEmail())
            .isPresent();

      boolean userNameExists = userProfileRepository
            .findByUsername(userProfile.getUsername())
            .isPresent();

      if (userEmailExists) {
         throw new IllegalStateException(ErrorMessages.EMAIL_TAKEN);
      }

      if (userNameExists) {
         throw new IllegalStateException(ErrorMessages.USERNAME_TAKEN);
      }

      userProfileRepository.save(userProfile);

      return true;
   }

   public AuthenticationResponse authenticate(AuthenticationRequest request) {
      authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                  request.getUsername(),
                  request.getPassword()));
      UserProfile user = userProfileRepository.findByUsername(request.getUsername()).orElseThrow(
            () -> new UsernameNotFoundException("Error, no user found"));
      String jwtToken = jwtService.generateToken(user);
      return new AuthenticationResponse(jwtToken);
   }
}
