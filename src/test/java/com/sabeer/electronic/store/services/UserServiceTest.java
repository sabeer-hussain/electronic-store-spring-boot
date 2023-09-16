package com.sabeer.electronic.store.services;

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

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
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
                .imageName("abc.png")
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
                .imageName("xyz.png")
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
        Path originalFilePath = fileSys.getPath(imagePath +"/abc.png");
        Path tempFilePath = fileSys.getPath(imagePath + "/temp.png");
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

        user.setImageName("xyz.png");
        Mockito.when(userRepository.findById("userIdabc")).thenReturn(Optional.of(user));
//        Mockito.doNothing().when(userRepository).delete(Mockito.any());

        userService.deleteUser(userId);

        Mockito.verify(userRepository, Mockito.times(1)).delete(user);
    }
}
