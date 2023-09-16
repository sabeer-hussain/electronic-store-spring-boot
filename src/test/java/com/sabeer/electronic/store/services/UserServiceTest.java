package com.sabeer.electronic.store.services;

import com.sabeer.electronic.store.dtos.PageableResponse;
import com.sabeer.electronic.store.dtos.UserDto;
import com.sabeer.electronic.store.entities.Role;
import com.sabeer.electronic.store.entities.User;
import com.sabeer.electronic.store.exceptions.ResourceNotFoundException;
import com.sabeer.electronic.store.repositories.RoleRepository;
import com.sabeer.electronic.store.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@SpringBootTest
public class UserServiceTest {

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper mapper;

    private User user;

    private Role role;

    @Value("${user.profile.image.path}")
    private String imagePath;

    @BeforeEach
    public void init() {
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
    }

    // create user
    @Test
    public void createUserTest() {
        Mockito.when(roleRepository.findById(Mockito.anyString())).thenReturn(Optional.of(role));
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);

        UserDto createdUser = userService.createUser(mapper.map(user, UserDto.class));

        System.out.println(createdUser.getName());

        Assertions.assertNotNull(createdUser);
        Assertions.assertEquals("Sabeer", createdUser.getName());
    }

    // update user test
    @Test
    public void updateUserTest() {
        String userId = "123";

        UserDto userDto = UserDto.builder()
                .name("Sabeer Hussain")
                .password("lcwd_new")
                .gender("Male")
                .about("This is updated user about details")
                .imageName("user_xyz.png")
                .build();

        Mockito.when(userRepository.findById(Mockito.anyString())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);

        UserDto updatedUser = userService.updateUser(userDto, userId);
        System.out.println(updatedUser.getName());
        System.out.println(updatedUser.getImageName());

        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals(userDto.getName(), updatedUser.getName(), "Name is not validated !!");
        Assertions.assertEquals(userDto.getImageName(), updatedUser.getImageName());
        // multiple assertions are valid..
    }

    @Test
    public void updateUser_ResourceNotFoundException_Test() {
        String userId = "123";

//        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(mapper.map(user, UserDto.class), userId));
    }

    // delete user test case
    @Test
    public void deleteUserTest() throws IOException {
        String userId = "userIdabc";

        Mockito.when(userRepository.findById("userIdabc")).thenReturn(Optional.of(user));
//        Mockito.doNothing().when(userRepository).delete(Mockito.any());

        File folder = new File(imagePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        FileSystem fileSys = FileSystems.getDefault();
        Path originalFilePath = fileSys.getPath(imagePath +"/user_abc.png");
        Path tempFilePath = fileSys.getPath(imagePath + "/user_temp.png");
        Files.copy(originalFilePath, tempFilePath);

        userService.deleteUser(userId);

        Mockito.verify(userRepository, Mockito.times(1)).delete(user);

        Files.copy(tempFilePath, originalFilePath);
        Files.delete(tempFilePath);
    }

    @Test
    public void deleteUser_ResourceNotFoundException_Test() {
        String userId = "userIdabc";

//        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(userId));
    }

    @Test
    public void deleteUser_NoSuchFileException_Test() {
        String userId = "userIdabc";

        user.setImageName("user_xyz.png");
        Mockito.when(userRepository.findById("userIdabc")).thenReturn(Optional.of(user));
//        Mockito.doNothing().when(userRepository).delete(Mockito.any());

        userService.deleteUser(userId);

        Mockito.verify(userRepository, Mockito.times(1)).delete(user);
    }

    // get all users
    @Test
    public void getAllUsersTest() {
        User user1 = User.builder()
                .name("Ankit")
                .email("ankit@gmail.com")
                .password("abcd")
                .gender("Male")
                .about("This is testing create method")
                .imageName("user_abc.png")
                .roles(Set.of(role))
                .build();

        User user2 = User.builder()
                .name("Uttam")
                .email("uttam@gmail.com")
                .password("abcd")
                .gender("Male")
                .about("This is testing create method")
                .imageName("user_abc.png")
                .roles(Set.of(role))
                .build();

        List<User> userList = List.of(user, user1, user2);
        Page<User> page = new PageImpl<>(userList);

        Mockito.when(userRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);

        PageableResponse<UserDto> allUsers = userService.getAllUsers(1, 2, "name", "asc");

        Assertions.assertEquals(3, allUsers.getContent().size());
    }

    @Test
    public void getAllUsers_SortByByNameInDescending_Test() {
        User user1 = User.builder()
                .name("Ankit")
                .email("ankit@gmail.com")
                .password("abcd")
                .gender("Male")
                .about("This is testing create method")
                .imageName("user_def.png")
                .roles(Set.of(role))
                .build();

        User user2 = User.builder()
                .name("Uttam")
                .email("uttam@gmail.com")
                .password("abcd")
                .gender("Male")
                .about("This is testing create method")
                .imageName("user_ghi.png")
                .roles(Set.of(role))
                .build();

        List<User> userList = List.of(user, user1, user2);
        Page<User> page = new PageImpl<>(userList);

        Mockito.when(userRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);

        PageableResponse<UserDto> allUsers = userService.getAllUsers(1, 2, "name", "desc");

        Assertions.assertEquals(3, allUsers.getContent().size());
    }

    // get user by id test case
    @Test
    public void getUserByIdTest() {
        String userId = "userIdTest";

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // actual call of service method
        UserDto userDto = userService.getUserById(userId);

        Assertions.assertNotNull(userDto);
        Assertions.assertEquals(user.getName(), userDto.getName(), "Name not matched !!");
    }

    @Test
    public void getUserById_ResourceNotFoundException_Test() {
//        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.getUserById("123"));
    }

    // get user by email test case
    @Test
    public void getUserByEmailTest() {
        String emailId = "sabeer@gmail.com";

        Mockito.when(userRepository.findByEmail(emailId)).thenReturn(Optional.of(user));

        UserDto userDto = userService.getUserByEmail(emailId);

        Assertions.assertNotNull(userDto);
        Assertions.assertEquals(user.getEmail(), userDto.getEmail(), "Email not matched !!");
    }

    @Test
    public void getUserByEmail_ResourceNotFoundException_Test() {
//        Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.getUserByEmail("sabeer@gmail.com"));
    }

    // search user test case
    @Test
    public void searchUserTest() {
        User user1 = User.builder()
                .name("Ankit Kumar")
                .email("ankit@gmail.com")
                .password("abcd")
                .gender("Male")
                .about("This is testing create method")
                .imageName("user_def.png")
                .roles(Set.of(role))
                .build();

        User user2 = User.builder()
                .name("Uttam Kumar")
                .email("uttam@gmail.com")
                .password("abcd")
                .gender("Male")
                .about("This is testing create method")
                .imageName("user_ghi.png")
                .roles(Set.of(role))
                .build();

        User user3 = User.builder()
                .name("Pankaj Kumar")
                .email("pankaj@gmail.com")
                .password("abcd")
                .gender("Male")
                .about("This is testing create method")
                .imageName("user_jkl.png")
                .roles(Set.of(role))
                .build();

        String keywords = "Kumar";
        Mockito.when(userRepository.findByNameContaining(keywords)).thenReturn(Arrays.asList(user, user1, user2, user3));

        List<UserDto> userDtos = userService.searchUser(keywords);

        Assertions.assertEquals(4, userDtos.size(), "Size not matched !!");
    }

    // find user by email test case
    @Test
    public void findUserByEmailOptionalTest() {
        String email = "sabeer@gmail.com";

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<User> userByEmailOptional = userService.findUserByEmailOptional(email);

        Assertions.assertTrue(userByEmailOptional.isPresent());
        User user1 = userByEmailOptional.get();
        Assertions.assertEquals(user.getEmail(), user1.getEmail(), "email does not matched !!");
    }
}
