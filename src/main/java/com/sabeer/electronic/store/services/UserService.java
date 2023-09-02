package com.sabeer.electronic.store.services;

import com.sabeer.electronic.store.dtos.UserDto;

import java.util.List;

public interface UserService {

    // create user
    UserDto createUser(UserDto userDto);

    // update user
    UserDto updateUser(UserDto userDto, String userId);

    // delete user
    void deleteUser(String userId);

    // get all users
    List<UserDto> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir);

    // get single user by id
    UserDto getUserById(String userId);

    // get single user by email
    UserDto getUserByEmail(String email);

    // search user
    List<UserDto> searchUser(String keyword);

    // other user specific features

}