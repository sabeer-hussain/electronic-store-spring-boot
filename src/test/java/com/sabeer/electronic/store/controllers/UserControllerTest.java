package com.sabeer.electronic.store.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabeer.electronic.store.dtos.PageableResponse;
import com.sabeer.electronic.store.dtos.UserDto;
import com.sabeer.electronic.store.entities.Role;
import com.sabeer.electronic.store.entities.User;
import com.sabeer.electronic.store.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private ModelMapper mapper;

    private User user;

    private Role role;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void init() throws Exception {
        role = Role.builder()
                .roleName("abc")
                .roleName("NORMAL")
                .build();
        user = User.builder()
                .name("Sabeer")
                .email("sabeer@gmail.com")
                .password("abcd")
                .gender("Male")
                .about("This is testing create method")
                .imageName("user_abc.png")
                .roles(Set.of(role))
                .build();

        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertObjectToJsonString(user))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    public void createUserTest() throws Exception {
//        /users + POST + user data as json
//        response: data as json + status created

        UserDto userDto = mapper.map(user, UserDto.class);
        Mockito.when(userService.createUser(Mockito.any())).thenReturn(userDto);

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                            .post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(convertObjectToJsonString(user))
                            .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    public void updateUserTest() throws Exception {
//        /users/{userId} + PUT + user data as json
//        response: data as json + status ok

        String userId = "123";
        UserDto userDto = mapper.map(user, UserDto.class);
        Mockito.when(userService.updateUser(Mockito.any(), Mockito.anyString())).thenReturn(userDto);

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .put("/users/" + userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkdXJnZXNoQGRldi5pbiIsImlhdCI6MTY5NDk2MTQwNSwiZXhwIjoxNjk0OTc5NDA1fQ.Z1wqRM_Zsgyw1s-UDXwYMyP0cBpugRT3ZVdkGx7f9pxeam3o8REhSr163QbENgnzikvR12D-kg4sn5gIfinlGQ")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertObjectToJsonString(user))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").exists());
    }

    // get all users : testing
    @Test
    public void getAllUsersTest() throws Exception {
        UserDto userDto1 = UserDto.builder().name("Durgesh").email("durgesh@gmail.com").password("durgesh").gender("Male").about("Testing").imageName("user_def.png").build();
        UserDto userDto2 = UserDto.builder().name("Amit").email("amit@gmail.com").password("amit").gender("Male").about("Testing").imageName("user_ghi.png").build();
        UserDto userDto3 = UserDto.builder().name("Sumit").email("sumit@gmail.com").password("sumit").gender("Male").about("Testing").imageName("user_jkl.png").build();
        UserDto userDto4 = UserDto.builder().name("Ankit").email("ankit@gmail.com").password("ankit").gender("Male").about("Testing").imageName("user_mno.png").build();

        PageableResponse<UserDto> pageableResponse = new PageableResponse<>();
        pageableResponse.setContent(Arrays.asList(userDto1, userDto2, userDto3, userDto4));
        pageableResponse.setPageNumber(100);
        pageableResponse.setPageSize(10);
        pageableResponse.setTotalElements(10000);
        pageableResponse.setTotalPages(1000);
        pageableResponse.setLastPage(false);
        Mockito.when(userService.getAllUsers(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString())).thenReturn(pageableResponse);

        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/users")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkdXJnZXNoQGRldi5pbiIsImlhdCI6MTY5NDk2MTQwNSwiZXhwIjoxNjk0OTc5NDA1fQ.Z1wqRM_Zsgyw1s-UDXwYMyP0cBpugRT3ZVdkGx7f9pxeam3o8REhSr163QbENgnzikvR12D-kg4sn5gIfinlGQ")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").exists());
    }

    private String convertObjectToJsonString(Object user) {
        try {
            return new ObjectMapper().writeValueAsString(user);
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
