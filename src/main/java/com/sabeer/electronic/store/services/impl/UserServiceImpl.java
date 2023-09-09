package com.sabeer.electronic.store.services.impl;

import com.sabeer.electronic.store.dtos.PageableResponse;
import com.sabeer.electronic.store.dtos.UserDto;
import com.sabeer.electronic.store.entities.Role;
import com.sabeer.electronic.store.entities.User;
import com.sabeer.electronic.store.exceptions.ResourceNotFoundException;
import com.sabeer.electronic.store.helper.Helper;
import com.sabeer.electronic.store.repositories.RoleRepository;
import com.sabeer.electronic.store.repositories.UserRepository;
import com.sabeer.electronic.store.services.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Value("${user.profile.image.path}")
    private String imagePath;

    @Value("${normal.role.id}")
    private String normalRoleId;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDto createUser(UserDto userDto) {
        // generate unique id in string format
        String userId = UUID.randomUUID().toString();
        userDto.setUserId(userId);
        // encoding password
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // dto -> entity
//        User user = dtoToEntity(userDto);
        User user = modelMapper.map(userDto, User.class);

        // fetch role of normal user and set it to user
        Role role = roleRepository.findById(normalRoleId).get();
        user.getRoles().add(role);

        User savedUser = userRepository.save(user);

        // entity -> dto
//        UserDto newDto = entityToDto(savedUser);
        UserDto newDto = modelMapper.map(savedUser, UserDto.class);
        return newDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto, String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with given id !!"));
        user.setName(userDto.getName());
        // email update

        // encoding password
        if (StringUtils.hasText(userDto.getPassword())) {
            userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        user.setAbout(userDto.getAbout());
        user.setGender(userDto.getGender());
        user.setImageName(userDto.getImageName());

        // update data
        User updatedUser = userRepository.save(user);

        // entity -> dto
//        UserDto updatedDto = entityToDto(updatedUser);
        UserDto updatedDto = modelMapper.map(updatedUser, UserDto.class);
        return updatedDto;
    }

    @Override
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with given id !!"));

        // delete user profile image
        // images/user/abc.png
        String fullPath = imagePath + user.getImageName();

        try {
            Path path = Paths.get(fullPath);
            Files.delete(path);
        } catch (NoSuchFileException ex) {
            LOGGER.error("User image not found in folder");
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // delete user
        userRepository.delete(user);
    }

    @Override
    public PageableResponse<UserDto> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir) {
//        List<User> users = userRepository.findAll();
//        List<UserDto> dtoList = users.stream().map(user -> entityToDto(user)).collect(Collectors.toList());
//        List<UserDto> dtoList = users.stream().map(user -> modelMapper.map(user, UserDto.class)).collect(Collectors.toList());
//        return dtoList;

        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());

        // pageNumber default starts from 0
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<User> page = userRepository.findAll(pageable);

        PageableResponse<UserDto> response = Helper.getPageableResponse(page, UserDto.class);

        return response;
    }

    @Override
    public UserDto getUserById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with given id !!"));

        // entity -> dto
//        return entityToDto(user);
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found with given email id !!"));

        // entity -> dto
//        return entityToDto(user);
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public List<UserDto> searchUser(String keyword) {
        List<User> users = userRepository.findByNameContaining(keyword);
//        List<UserDto> dtoList = users.stream().map(user -> entityToDto(user)).collect(Collectors.toList());
        List<UserDto> dtoList = users.stream().map(user -> modelMapper.map(user, UserDto.class)).collect(Collectors.toList());
        return dtoList;
    }

    private UserDto entityToDto(User savedUser) {
        UserDto userDto = UserDto.builder()
                                    .userId(savedUser.getUserId())
                                    .name(savedUser.getName())
                                    .email(savedUser.getEmail())
                                    .password(savedUser.getPassword())
                                    .about(savedUser.getAbout())
                                    .gender(savedUser.getGender())
                                    .imageName(savedUser.getImageName())
                                .build();
        return userDto;
    }

    private User dtoToEntity(UserDto userDto) {
        User user = User.builder()
                            .userId(userDto.getUserId())
                            .name(userDto.getName())
                            .email(userDto.getEmail())
                            .password(userDto.getPassword())
                            .about(userDto.getAbout())
                            .gender(userDto.getGender())
                            .imageName(userDto.getImageName())
                        .build();
        return user;
    }
}
