package com.sabeer.electronic.store.controllers;

import com.sabeer.electronic.store.dtos.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ModelMapper mapper;

    @GetMapping("/current-username")
    public ResponseEntity<String> getCurrentUserName(Principal principal) {
        String name = principal.getName();
        return new ResponseEntity<>(name, HttpStatus.OK);
    }

    @GetMapping("/current")
    public ResponseEntity<UserDto> getCurrentUser(Principal principal) {
        String name = principal.getName();
        UserDetails userDetails = userDetailsService.loadUserByUsername(name);
        return new ResponseEntity<>(mapper.map(userDetails, UserDto.class), HttpStatus.OK);
    }
}
