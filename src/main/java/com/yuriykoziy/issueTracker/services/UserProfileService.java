package com.yuriykoziy.issueTracker.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.yuriykoziy.issueTracker.constants.ErrorMessages;
import com.yuriykoziy.issueTracker.dto.UserProfileDto;
import com.yuriykoziy.issueTracker.exceptions.UserAlreadyExistException;
import com.yuriykoziy.issueTracker.exceptions.UserNotFoundException;
import com.yuriykoziy.issueTracker.models.UserProfile;
import com.yuriykoziy.issueTracker.repositories.UserProfileRepository;

import lombok.AllArgsConstructor;

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
        UserProfile user = userProfileRepository.findById(id)
                .orElseThrow(
                        () -> new UserNotFoundException(String.format(ErrorMessages.USER_ID_NOT_FOUND, id)));
        return modelMapper.map(user, UserProfileDto.class);
    }

    public boolean updateProfile(UserProfileDto user, Long userId) {
        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessages.NO_USER_FOUND));

        String newEmail = user.getEmail();
        String newUsername = user.getUsername();

        if (!newEmail.equals(userProfile.getEmail()) && userProfileRepository.findByEmail(newEmail).isPresent()) {
            throw new UserAlreadyExistException(ErrorMessages.EMAIL_TAKEN);
        }

        if (!newUsername.equals(userProfile.getUsername())
                && userProfileRepository.findByUsername(newUsername).isPresent()) {
            throw new UserAlreadyExistException(ErrorMessages.USERNAME_TAKEN);
        }

        modelMapper.map(user, userProfile);
        userProfile.setUpdatedOn(LocalDateTime.now());
        userProfileRepository.save(userProfile);
        return true;
    }

    public Page<UserProfileDto> getAllUsers(int page, int size) {
        Pageable paging = PageRequest.of(page, size);
        Page<UserProfile> usersPage = userProfileRepository.findAll(paging);
        return usersPage.map(user -> modelMapper.map(user, UserProfileDto.class));
    }

    public Page<UserProfileDto> getAllUsersByBanned(boolean isEnabled, int page, int size) {
        Pageable paging = PageRequest.of(page, size);
        Page<UserProfile> usersPage = userProfileRepository.findByEnabled(!isEnabled, paging);
        return usersPage.map(user -> modelMapper.map(user, UserProfileDto.class));

    }

    public Page<UserProfileDto> findAllCriteria(
            Boolean enabled,
            Boolean locked,
            int page,
            int size) {
        Pageable paging = PageRequest.of(page, size);
        Page<UserProfile> usersPage = userProfileRepository.findByCriteria(enabled, locked, paging);
        return usersPage.map(user -> modelMapper.map(user, UserProfileDto.class));

    }

    public boolean banUser(UserProfileDto user) {
        UserProfile userProfile = userProfileRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new UserNotFoundException(ErrorMessages.NO_USER_FOUND));

        userProfile.setEnabled(false);
        userProfile.setDisabledOn(LocalDateTime.now());
        userProfileRepository.save(userProfile);
        return true;
    }
}
