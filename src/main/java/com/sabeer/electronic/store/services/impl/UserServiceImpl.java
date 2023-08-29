package com.sabeer.electronic.store.services.impl;

import com.sabeer.electronic.store.dtos.UserDto;
import com.sabeer.electronic.store.entities.User;
import com.sabeer.electronic.store.repositories.UserRepository;
import com.sabeer.electronic.store.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        // generate unique id in string format
        String userId = UUID.randomUUID().toString();
        userDto.setUserId(userId);

        // dto -> entity
//        User user = dtoToEntity(userDto);
        User user = modelMapper.map(userDto, User.class);
        User savedUser = userRepository.save(user);

        // entity -> dto
//        UserDto newDto = entityToDto(savedUser);
        UserDto newDto = modelMapper.map(savedUser, UserDto.class);
        return newDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto, String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException(("User not found with given id !!")));
        user.setName(userDto.getName());
        // email update
        user.setPassword(userDto.getPassword());
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
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException(("User not found with given id !!")));
        // delete user
        userRepository.delete(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
//        List<UserDto> dtoList = users.stream().map(user -> entityToDto(user)).collect(Collectors.toList());
        List<UserDto> dtoList = users.stream().map(user -> modelMapper.map(user, UserDto.class)).collect(Collectors.toList());
        return dtoList;
    }

    @Override
    public UserDto getUserById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException(("User not found with given id !!")));

        // entity -> dto
//        return entityToDto(user);
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException(("User not found with given email id !!")));

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
