package com.sabeer.electronic.store.services;

import com.sabeer.electronic.store.dtos.UserDto;
import com.sabeer.electronic.store.entities.Role;
import com.sabeer.electronic.store.entities.User;
import com.sabeer.electronic.store.repositories.RoleRepository;
import com.sabeer.electronic.store.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

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
}
