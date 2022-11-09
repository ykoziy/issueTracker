package com.yuriykoziy.issueTracker.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping(path = "api/v1/basicauth")
public class AuthController {

    @GetMapping(produces = "text/plain")
    public String basicauth() {
        return "you are logged in";
    }
}
