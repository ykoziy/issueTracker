package com.yuriykoziy.issueTracker.controllers;
import com.yuriykoziy.issueTracker.dto.RegistrationDto;
import com.yuriykoziy.issueTracker.services.RegisterService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping(path = "api/v1/registration")
@AllArgsConstructor
public class RegisterController {
    private RegisterService registerService;

    @PostMapping
    public String register(@RequestBody RegistrationDto request) {
        return registerService.register(request);
    }
}
