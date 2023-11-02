package com.sabeer.electronic.store.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Tag(name = "HomeController", description = "REST APIs related to testing !!")
@SecurityRequirement(name = "bearerScheme")
public class HomeController {

    @GetMapping
    public String testing() {
        return "Welcome to electronic store";
    }
}
