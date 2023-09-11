package com.sabeer.electronic.store.controllers;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Api(value = "HomeController", description = "REST APIs related to testing !!")
public class HomeController {

    @GetMapping
    public String testing() {
        return "Welcome to electronic store";
    }
}
