package com.yuriykoziy.issueTracker.services;

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

import java.util.Optional;

// get user related data
@Service
@AllArgsConstructor
public class UserProfileService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG = "user with email %s is not found";

    private final static String USER_ID_NOT_FOUND_MSG = "user with id %s is not found";
    private final UserProfileRepository userProfileRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final ModelMapper modelMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userProfileRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, username)));
    }

    public String register(UserProfile userProfile) {
        boolean userEmailExists = userProfileRepository
                .findByEmail(userProfile.getEmail())
                .isPresent();

        boolean userNameExists = userProfileRepository
                .findByUsername(userProfile.getUsername())
                .isPresent();

        if (userEmailExists) {
            throw new IllegalStateException("email already taken");
        }

        if (userNameExists) {
            throw new IllegalStateException("username already taken");
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
            throw new IllegalStateException(String.format(USER_ID_NOT_FOUND_MSG, id));
        }
        UserProfile user = userOptional.get();
        return modelMapper.map(user, UserProfileDto.class);
    }

    public boolean updateProfile(UserProfileDto user) {
        Optional<UserProfile> userOptional = userProfileRepository.findByEmail(user.getEmail());
        if (!userOptional.isPresent()) {
            throw new IllegalStateException("no user found");
        }
        UserProfile userProfile = userOptional.get();
        modelMapper.map(user, userProfile);
        userProfileRepository.save(userProfile);
        return true;
    }
}
