package com.sabeer.electronic.store.controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.sabeer.electronic.store.dtos.JwtRequest;
import com.sabeer.electronic.store.dtos.JwtResponse;
import com.sabeer.electronic.store.dtos.UserDto;
import com.sabeer.electronic.store.entities.User;
import com.sabeer.electronic.store.exceptions.BadApiRequestException;
import com.sabeer.electronic.store.security.JwtHelper;
import com.sabeer.electronic.store.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

@RestController
@RequestMapping("/auth")
//@CrossOrigin(
//        origins = "http://localhost:4200",
//        allowedHeaders = {"Authorization"},
//        methods = {RequestMethod.GET, RequestMethod.POST},
//        maxAge = 3600
//)
@Tag(name = "AuthController", description = "APIs for Authentication !!")
@SecurityRequirement(name = "bearerScheme")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Value("${googleClientId}")
    private String googleClientId;

    @Value("${newPassword}")
    private String newPassword;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private ModelMapper mapper;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {
        this.doAuthenticate(request.getEmail(), request.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = this.jwtHelper.generateToken(userDetails);

        UserDto userDto = mapper.map(userDetails, UserDto.class);

        JwtResponse response = JwtResponse.builder()
                .jwtToken(token)
                .user(userDto)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

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

    // login with Google api
    @PostMapping("/google")
    public ResponseEntity<JwtResponse> loginWithGoogle(@RequestBody Map<String, Object> data) throws IOException {

        // get the id token from request
        String idToken = data.get("idToken").toString();

        // call google api
        NetHttpTransport netHttpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        GoogleIdTokenVerifier.Builder verifier = new GoogleIdTokenVerifier.Builder(netHttpTransport, jsonFactory).setAudience(Collections.singleton(googleClientId));

        GoogleIdToken googleIdToken = GoogleIdToken.parse(verifier.getJsonFactory(), idToken);

        GoogleIdToken.Payload payload = googleIdToken.getPayload();

        LOGGER.info("Payload : {}", payload);

        String email = payload.getEmail();

        User user = null;

        user = userService.findUserByEmailOptional(email).orElse(null);

        if (user == null) {
            // create new user
            user = this.saveUser(email, data.get("name").toString(), data.get("photoUrl").toString());
        }

        ResponseEntity<JwtResponse> jwtResponseResponseEntity = this.login(JwtRequest.builder().email(user.getEmail()).password(newPassword).build());

        return jwtResponseResponseEntity;
    }

    private User saveUser(String email, String name, String photoUrl) {
        UserDto newUser = UserDto.builder()
                .name(name)
                .email(email)
                .password(newPassword)
                .imageName(photoUrl)
                .roles(new HashSet<>())
                .build();
        UserDto user = userService.createUser(newUser);

        return this.mapper.map(user, User.class);
    }

    private void doAuthenticate(String email, String password) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);
        try {
            authenticationManager.authenticate(authentication);
        } catch (AuthenticationException ex) {
            throw new BadApiRequestException("Invalid Username or Password !!");
        }
    }
}
