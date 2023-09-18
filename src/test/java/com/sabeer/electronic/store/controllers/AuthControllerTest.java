package com.sabeer.electronic.store.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabeer.electronic.store.dtos.JwtRequest;
import com.sabeer.electronic.store.dtos.JwtResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    private JwtRequest jwtRequest;

    @Autowired
    private MockMvc mockMvc;

    private static String JWT_TOKEN;

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

        // actual request for url
        MvcResult mvcResult = this.mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(jwtRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken").exists())
                .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        JwtResponse jwtResponse = new ObjectMapper().readValue(content, JwtResponse.class);
        JWT_TOKEN = jwtResponse.getJwtToken();
    }
}
