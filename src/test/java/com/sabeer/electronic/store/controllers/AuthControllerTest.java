package com.sabeer.electronic.store.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabeer.electronic.store.dtos.JwtRequest;
import com.sabeer.electronic.store.entities.Role;
import com.sabeer.electronic.store.entities.User;
import com.sabeer.electronic.store.security.JwtHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @MockBean
    private JwtHelper jwtHelper;

    private JwtRequest jwtRequest;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void init() throws Exception {
        jwtRequest = JwtRequest.builder()
                .email("msabeerhussain007@gmail.com")
                .password("abcd")
                .build();
    }

    @Test
    public void loginTest() throws Exception {
//        /auth/login + POST + user data as json
//        response: data as json + status ok

        mockSecurity();

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(jwtRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken").exists());
    }

    @Test
    public void getCurrentUsernameTest() throws Exception {
        mockSecurity();

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/auth/current-username")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTQ5NjY1OTgsImV4cCI6MTY5NDk4NDU5OH0.pjsyYMbxOneNF36OJlgOZ_uaf1ARC1ulVyhmtOmSgRmE31iPkV5KAMNdrnRZjY1L1wArK_VAg9m7OrF_Qw9srw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("msabeerhussain007@gmail.com"));
    }

    @Test
    public void getCurrentUserTest() throws Exception {
        mockSecurity();

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/auth/current")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTQ5NjY1OTgsImV4cCI6MTY5NDk4NDU5OH0.pjsyYMbxOneNF36OJlgOZ_uaf1ARC1ulVyhmtOmSgRmE31iPkV5KAMNdrnRZjY1L1wArK_VAg9m7OrF_Qw9srw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.name").exists());
    }

    private void mockSecurity() {
        Mockito.when(jwtHelper.getUsernameFromToken(Mockito.any())).thenReturn("msabeerhussain007@gmail.com");

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserDetailsService userDetailsService = Mockito.mock(UserDetailsService.class);
        Role role = Role.builder()
                .roleName("wetrsdfwetwfasfwdf")
                .roleName("ROLE_ADMIN")
                .build();
        User user = User.builder()
                .userId("05a268c9-e80b-45a7-8ad1-eaefc17a08ce")
                .name("Sabeer Hussain")
                .email("msabeerhussain007@gmail.com")
                .password("abcd")
                .gender("Male")
                .about("I am java developer")
                .imageName("0b70a62d-3f32-4599-95a4-3d932c7605fd.jpg")
                .roles(Set.of(role))
                .build();
        Mockito.when(userDetailsService.loadUserByUsername(Mockito.any())).thenReturn(user);

        AuthenticationManager authenticationManager = Mockito.mock(AuthenticationManager.class);
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);

        Mockito.when(jwtHelper.generateToken(Mockito.any())).thenReturn("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTQ5NjY1OTgsImV4cCI6MTY5NDk4NDU5OH0.pjsyYMbxOneNF36OJlgOZ_uaf1ARC1ulVyhmtOmSgRmE31iPkV5KAMNdrnRZjY1L1wArK_VAg9m7OrF_Qw9srw");

        Mockito.when(jwtHelper.validateToken(Mockito.anyString(), Mockito.any())).thenReturn(true);
    }
}
