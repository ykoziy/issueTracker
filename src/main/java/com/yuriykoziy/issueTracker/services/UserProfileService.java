package com.yuriykoziy.issueTracker.services;

import com.yuriykoziy.issueTracker.constants.ErrorMessages;
import com.yuriykoziy.issueTracker.dto.UserProfileDto;
import com.yuriykoziy.issueTracker.models.UserProfile;
import com.yuriykoziy.issueTracker.repositories.UserProfileRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// get user related data
@Service
@AllArgsConstructor
public class UserProfileService implements UserDetailsService {
    private final UserProfileRepository userProfileRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final ModelMapper modelMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userProfileRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(ErrorMessages.USER_NOT_FOUND, username)));
    }

    public String register(UserProfile userProfile) {
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

        String encodedPassword = bCryptPasswordEncoder.encode(userProfile.getPassword());

        userProfile.setPassword(encodedPassword);

        userProfileRepository.save(userProfile);

        // TODO: send confirmation token

        return "it works";
    }

    public UserProfileDto getUserProfileById(Long id) {
        Optional<UserProfile> userOptional = userProfileRepository.findById(id);
        if (!userOptional.isPresent()) {
            throw new IllegalStateException(String.format(ErrorMessages.USER_ID_NOT_FOUND, id));
        }
        UserProfile user = userOptional.get();
        return modelMapper.map(user, UserProfileDto.class);
    }

    public boolean updateProfile(UserProfileDto user) {
        Optional<UserProfile> userOptional = userProfileRepository.findByEmail(user.getEmail());
        if (!userOptional.isPresent()) {
            throw new IllegalStateException(ErrorMessages.userNotFound);
        }
        UserProfile userProfile = userOptional.get();
        modelMapper.map(user, userProfile);
        userProfileRepository.save(userProfile);
        return true;
    }

    public List<UserProfileDto> getAllUsers() {
        return userProfileRepository.findAll().stream().map(user -> modelMapper.map(user, UserProfileDto.class))
                .collect(Collectors.toList());
    }

    public boolean banUser(UserProfileDto user) {
        Optional<UserProfile> userOptional = userProfileRepository.findByEmail(user.getEmail());
        if (!userOptional.isPresent()) {
            throw new IllegalStateException(ErrorMessages.userNotFound);
        }
        UserProfile userProfile = userOptional.get();
        userProfile.setLocked(true);
        modelMapper.map(user, userProfile);
        userProfileRepository.save(userProfile);
        return true;
    }
}
