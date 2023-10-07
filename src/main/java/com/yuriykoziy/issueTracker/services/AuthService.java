package com.yuriykoziy.issueTracker.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.yuriykoziy.issueTracker.constants.ErrorMessages;
import com.yuriykoziy.issueTracker.controllers.auth.AuthenticationRequest;
import com.yuriykoziy.issueTracker.controllers.auth.AuthenticationResponse;
import com.yuriykoziy.issueTracker.dto.RegistrationDto;
import com.yuriykoziy.issueTracker.enums.UserRole;
import com.yuriykoziy.issueTracker.exceptions.TokenExpiredException;
import com.yuriykoziy.issueTracker.exceptions.UserAlreadyExistException;
import com.yuriykoziy.issueTracker.exceptions.ValidationException;
import com.yuriykoziy.issueTracker.models.UserProfile;
import com.yuriykoziy.issueTracker.models.VerificationToken;
import com.yuriykoziy.issueTracker.repositories.UserProfileRepository;
import com.yuriykoziy.issueTracker.repositories.VerificationTokenRepository;
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
   private VerificationTokenRepository tokenRepository;
   private EmailService emailService;

   private static final int MAX_ATTEMPTS = 3;

   public boolean register(RegistrationDto request) {
      boolean isValidEmail = emailValidator.test(request.getEmail());
      if (!isValidEmail) {
         throw new ValidationException(ErrorMessages.INVALID_EMAIL);
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
         throw new UserAlreadyExistException(ErrorMessages.EMAIL_TAKEN);
      }

      if (userNameExists) {
         throw new UserAlreadyExistException(ErrorMessages.USERNAME_TAKEN);
      }
      userProfileRepository.save(userProfile);

      // Generate a verification token and save it
      String token = generateVerificationToken(userProfile);
      tokenRepository.save(new VerificationToken(token, userProfile));

      // Send the verification email
      emailService.sendVerificationEmail(userProfile.getEmail(), token);

      return true;
   }

   public AuthenticationResponse authenticate(AuthenticationRequest request) {
      UserProfile user = userProfileRepository.findByUsername(request.getUsername()).orElseThrow(
            () -> new UsernameNotFoundException("Error, no user found"));

      int failedAttempts = user.getFailedLoginAttempts();
      if (failedAttempts >= 3) {
         throw new BadCredentialsException("Invalid username or password.");
      }

      try {
         authenticationManager.authenticate(
               new UsernamePasswordAuthenticationToken(
                     request.getUsername(),
                     request.getPassword()));
         user.setFailedLoginAttempts(0);
         userProfileRepository.save(user);
      } catch (BadCredentialsException e) {
         failedAttempts++;
         user.setFailedLoginAttempts(failedAttempts);
         userProfileRepository.save(user);
         if (failedAttempts >= MAX_ATTEMPTS) {
            user.setLocked(true);
            user.setLockedOn(LocalDateTime.now());
            userProfileRepository.save(user);
         }
         throw new BadCredentialsException("Invalid username or password.");
      }

      String jwtToken = jwtService.generateToken(user);
      return new AuthenticationResponse(jwtToken);
   }

   public String generateVerificationToken(UserProfile user) {
      // Generate a unique verification token
      return UUID.randomUUID().toString();
   }

   public void verifyEmail(String token) {
      // Find the verification token in the database
      VerificationToken verificationToken = tokenRepository.findByToken(token);

      // Check if the token is valid and not expired
      if (verificationToken == null || verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
         throw new TokenExpiredException("Token is invalid or expired.");
      }

      // Mark the user as verified
      UserProfile user = verificationToken.getUser();
      user.setVerified(true);
      userProfileRepository.save(user);

      // Delete the verification token
      tokenRepository.delete(verificationToken);
   }
}
