package com.yuriykoziy.issueTracker.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.yuriykoziy.issueTracker.constants.ErrorMessages;
import com.yuriykoziy.issueTracker.dto.UserProfileDto;
import com.yuriykoziy.issueTracker.exceptions.UserAlreadyExistException;
import com.yuriykoziy.issueTracker.exceptions.UserNotFoundException;
import com.yuriykoziy.issueTracker.models.UserProfile;
import com.yuriykoziy.issueTracker.repositories.UserProfileRepository;

import lombok.AllArgsConstructor;

// get user related data
@Service
@AllArgsConstructor
public class UserProfileService implements UserDetailsService {
    private final UserProfileRepository userProfileRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserProfile loadUserByUsername(String username) throws UserNotFoundException {
        return userProfileRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UserNotFoundException(String.format(ErrorMessages.USER_NOT_FOUND, username)));
    }

    public UserProfileDto getUserProfileById(Long id) {
        Optional<UserProfile> userOptional = userProfileRepository.findById(id);
        if (!userOptional.isPresent()) {
            throw new UserNotFoundException(String.format(ErrorMessages.USER_ID_NOT_FOUND, id));
        }
        UserProfile user = userOptional.get();
        return modelMapper.map(user, UserProfileDto.class);
    }

    public boolean updateProfile(UserProfileDto user, Long userId) {
        Optional<UserProfile> userOptional = userProfileRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new UserNotFoundException(ErrorMessages.NO_USER_FOUND);
        }
        UserProfile userProfile = userOptional.get();

        if (!user.getEmail().equals(userProfile.getEmail())) {
            Optional<UserProfile> checkEmail = userProfileRepository.findByEmail(user.getEmail());
            if (checkEmail.isPresent()) {
                throw new UserAlreadyExistException(ErrorMessages.EMAIL_TAKEN);
            }
        }

        if (!user.getUsername().equals(userProfile.getUsername())) {
            Optional<UserProfile> checkUsername = userProfileRepository.findByUsername(user.getUsername());
            if (checkUsername.isPresent()) {
                throw new UserAlreadyExistException(ErrorMessages.USERNAME_TAKEN);
            }
        }

        modelMapper.map(user, userProfile);
        userProfileRepository.save(userProfile);
        return true;
    }

    public List<UserProfileDto> getAllUsers() {
        return userProfileRepository.findAll().stream().map(user -> modelMapper.map(user, UserProfileDto.class))
                .collect(Collectors.toList());
    }

    public List<UserProfileDto> getAllUsersByBanned(boolean isEnabled) {
        return userProfileRepository.findByEnabled(!isEnabled).stream()
                .map(user -> modelMapper.map(user, UserProfileDto.class))
                .collect(Collectors.toList());
    }

    public boolean banUser(UserProfileDto user) {
        Optional<UserProfile> userOptional = userProfileRepository.findByEmail(user.getEmail());
        if (!userOptional.isPresent()) {
            throw new UserNotFoundException(ErrorMessages.NO_USER_FOUND);
        }
        UserProfile userProfile = userOptional.get();
        userProfile.setEnabled(false);
        userProfileRepository.save(userProfile);
        return true;
    }
}
