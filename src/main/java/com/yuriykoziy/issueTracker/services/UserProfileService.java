package com.yuriykoziy.issueTracker.services;

import com.yuriykoziy.issueTracker.models.UserProfile;
import com.yuriykoziy.issueTracker.repositories.UserProfileRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

// get user related data
@Service
@AllArgsConstructor
public class UserProfileService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG = "user with email %s is not found";
    private final UserProfileRepository userProfileRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userProfileRepository.findByUserName(email).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }

    public String register(UserProfile userProfile) {
        boolean userEmailExists = userProfileRepository
                .findByEmail(userProfile.getEmail())
                .isPresent();

        boolean userNameExists = userProfileRepository
                .findByUserName(userProfile.getUsername())
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
}
