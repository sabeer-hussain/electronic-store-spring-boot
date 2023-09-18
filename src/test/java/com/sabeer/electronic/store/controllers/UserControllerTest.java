package com.sabeer.electronic.store.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabeer.electronic.store.dtos.PageableResponse;
import com.sabeer.electronic.store.dtos.RoleDto;
import com.sabeer.electronic.store.dtos.UserDto;
import com.sabeer.electronic.store.services.FileService;
import com.sabeer.electronic.store.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @MockBean
    private UserService userService;

    @MockBean
    private FileService fileService;

    private UserDto userDto;

    @Value("${user.profile.image.path}")
    private String imagePath;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        RoleDto role = RoleDto.builder()
                .roleName("abc")
                .roleName("NORMAL")
                .build();
        userDto = UserDto.builder()
                .name("Sabeer Hussain")
                .email("msabeerhussain007@gmail.com")
                .password("abcd")
                .gender("Male")
                .about("This is testing create method")
                .imageName("user_abc.png")
                .roles(Set.of(role))
                .build();
    }

    @Test
    public void createUserTest() throws Exception {
//        /users + POST + user data as json
//        response: data as json + status created

        Mockito.when(userService.createUser(Mockito.any())).thenReturn(userDto);

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertObjectToJsonString(userDto))
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
        Mockito.when(userService.updateUser(Mockito.any(), Mockito.anyString())).thenReturn(userDto);

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .put("/users/" + userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTQ5NjY1OTgsImV4cCI6MTY5NDk4NDU5OH0.pjsyYMbxOneNF36OJlgOZ_uaf1ARC1ulVyhmtOmSgRmE31iPkV5KAMNdrnRZjY1L1wArK_VAg9m7OrF_Qw9srw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertObjectToJsonString(userDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    public void deleteUserTest() throws Exception {
        String userId = "123";
        Mockito.doNothing().when(userService).deleteUser(Mockito.anyString());

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .delete("/users/" + userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTQ5NjY1OTgsImV4cCI6MTY5NDk4NDU5OH0.pjsyYMbxOneNF36OJlgOZ_uaf1ARC1ulVyhmtOmSgRmE31iPkV5KAMNdrnRZjY1L1wArK_VAg9m7OrF_Qw9srw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
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
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTQ5NjY1OTgsImV4cCI6MTY5NDk4NDU5OH0.pjsyYMbxOneNF36OJlgOZ_uaf1ARC1ulVyhmtOmSgRmE31iPkV5KAMNdrnRZjY1L1wArK_VAg9m7OrF_Qw9srw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").exists());
    }

    @Test
    public void getUserTest() throws Exception {
        String userId = "123";
        Mockito.when(userService.getUserById(Mockito.anyString())).thenReturn(userDto);

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/users/" + userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTQ5NjY1OTgsImV4cCI6MTY5NDk4NDU5OH0.pjsyYMbxOneNF36OJlgOZ_uaf1ARC1ulVyhmtOmSgRmE31iPkV5KAMNdrnRZjY1L1wArK_VAg9m7OrF_Qw9srw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    public void getUserByEmailTest() throws Exception {
        String email = "durgesh@dev.in";
        Mockito.when(userService.getUserByEmail(Mockito.anyString())).thenReturn(userDto);

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/users/email/" + email)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTQ5NjY1OTgsImV4cCI6MTY5NDk4NDU5OH0.pjsyYMbxOneNF36OJlgOZ_uaf1ARC1ulVyhmtOmSgRmE31iPkV5KAMNdrnRZjY1L1wArK_VAg9m7OrF_Qw9srw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    public void searchUserTest() throws Exception {
        String keywords = "kumar";
        UserDto userDto = UserDto.builder()
                .name("Sabeer Hussain")
                .email("msabeerhussain007@gmail.com")
                .password("abcd")
                .gender("Male")
                .about("This is testing create method")
                .imageName("user_abc.png")
                .build();
        List<UserDto> userDtoList = List.of(userDto);
        Mockito.when(userService.searchUser(Mockito.anyString())).thenReturn(userDtoList);

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/users/search/" + keywords)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTQ5NjY1OTgsImV4cCI6MTY5NDk4NDU5OH0.pjsyYMbxOneNF36OJlgOZ_uaf1ARC1ulVyhmtOmSgRmE31iPkV5KAMNdrnRZjY1L1wArK_VAg9m7OrF_Qw9srw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").exists());
    }

    @Test
    public void uploadUserImageTest() throws Exception {
        String userId = "123";
        String imageName = "user_abc.png";
        UserDto userDto = UserDto.builder()
                .name("Sabeer Hussain")
                .email("msabeerhussain007@gmail.com")
                .password("abcd")
                .gender("Male")
                .about("This is testing create method")
                .imageName("user_abc.png")
                .build();
        Mockito.when(fileService.uploadFile(Mockito.any(MultipartFile.class), Mockito.anyString())).thenReturn(imageName);
        Mockito.when(userService.getUserById(Mockito.anyString())).thenReturn(userDto);
        Mockito.when(userService.updateUser(Mockito.any(), Mockito.anyString())).thenReturn(userDto);

        String name = "userImage";
        String originalFileName = "user_abc.png";
        String contentType = "image/jpeg";
        byte[] content = null;
        try {
            content = Files.readAllBytes(Paths.get(imagePath+ name));
        } catch (final IOException e) {
        }

        MockMultipartFile multipartFile = new MockMultipartFile(name, originalFileName, contentType, content);


        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .multipart("/users/image/" + userId)
                        .file(multipartFile)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTQ5NjY1OTgsImV4cCI6MTY5NDk4NDU5OH0.pjsyYMbxOneNF36OJlgOZ_uaf1ARC1ulVyhmtOmSgRmE31iPkV5KAMNdrnRZjY1L1wArK_VAg9m7OrF_Qw9srw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void serveUserImageTest() throws Exception {
        String userId = "123";
        UserDto userDto = UserDto.builder()
                .name("Sabeer Hussain")
                .email("msabeerhussain007@gmail.com")
                .password("abcd")
                .gender("Male")
                .about("This is testing create method")
                .imageName("user_abc.png")
                .build();
        Mockito.when(userService.getUserById(Mockito.anyString())).thenReturn(userDto);
        FileInputStream inputStream = new FileInputStream(imagePath + "user_abc.png");
        Mockito.when(fileService.getResource(Mockito.anyString(), Mockito.anyString())).thenReturn(inputStream);

        // actual request for url
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/users/image/" + userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtc2FiZWVyaHVzc2FpbjAwN0BnbWFpbC5jb20iLCJpYXQiOjE2OTQ5NjY1OTgsImV4cCI6MTY5NDk4NDU5OH0.pjsyYMbxOneNF36OJlgOZ_uaf1ARC1ulVyhmtOmSgRmE31iPkV5KAMNdrnRZjY1L1wArK_VAg9m7OrF_Qw9srw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
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
