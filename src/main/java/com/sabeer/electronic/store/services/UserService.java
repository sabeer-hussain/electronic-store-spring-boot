package com.sabeer.electronic.store.services;

import com.sabeer.electronic.store.dtos.PageableResponse;
import com.sabeer.electronic.store.dtos.UserDto;
import com.sabeer.electronic.store.entities.User;

import java.util.Optional;

public interface UserService {

    // create user
    UserDto createUser(UserDto userDto);

    // update user
    UserDto updateUser(UserDto userDto, String userId);

    // delete user
    void deleteUser(String userId);

    // get all users
    PageableResponse<UserDto> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir);

    // get single user by id
    UserDto getUserById(String userId);

    // get single user by email
    UserDto getUserByEmail(String email);

    // search user
    PageableResponse<UserDto> searchUser(String keyword, int pageNumber, int pageSize, String sortBy, String sortDir);

    Optional<User> findUserByEmailOptional(String email);

    // other user specific features

}
