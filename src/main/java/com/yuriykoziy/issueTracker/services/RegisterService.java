package com.yuriykoziy.issueTracker.services;

import com.yuriykoziy.issueTracker.constants.ErrorMessages;
import com.yuriykoziy.issueTracker.dto.RegistrationDto;
import com.yuriykoziy.issueTracker.enums.UserRole;
import com.yuriykoziy.issueTracker.models.UserProfile;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegisterService {
    private final EmailValidator emailValidator;
    private final UserProfileService userProfileService;

    public String register(RegistrationDto request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());
        if (!isValidEmail) {
            throw new IllegalStateException(ErrorMessages.INVALID_EMAIL);
        }
        return userProfileService.register(
                new UserProfile(
                        request.getFirstName(),
                        request.getLastName(),
                        request.getEmail(),
                        request.getUsername(),
                        request.getPassword(),
                        UserRole.USER
                )
        );
    }
}
