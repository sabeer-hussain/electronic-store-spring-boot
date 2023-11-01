package com.sabeer.electronic.store.controllers;

import com.sabeer.electronic.store.dtos.*;
import com.sabeer.electronic.store.services.FileService;
import com.sabeer.electronic.store.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/users")
//@CrossOrigin("*")
@Tag(name = "UserController", description = "REST APIs related to perform user operations !!")
public class UserController {

    private Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Value("${user.profile.image.path}")
    private String imageUploadPath;

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    // create
    @PostMapping
    @Operation(summary = "create new user !!", description = "this is user api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success | OK"),
            @ApiResponse(responseCode = "201", description = "new user created !!"),
            @ApiResponse(responseCode = "401", description = "not authorized !!")
    })
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        UserDto createdUserDto = userService.createUser(userDto);
        return new ResponseEntity<>(createdUserDto, HttpStatus.CREATED);
    }

    // update
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable("userId") String userId,
            @Valid @RequestBody UserDto userDto) {
        UserDto updatedUserDto = userService.updateUser(userDto, userId);
        return new ResponseEntity<>(updatedUserDto, HttpStatus.OK);
    }

    // delete
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponseMessage> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        ApiResponseMessage message = ApiResponseMessage.builder()
                                        .message("User is deleted Successfully !!")
                                        .success(true)
                                        .status(HttpStatus.OK)
                                    .build();
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    // get all
    @GetMapping
    @Operation(summary = "get all users")
    public ResponseEntity<PageableResponse<UserDto>> getAllUsers(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "name", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        return new ResponseEntity<>(userService.getAllUsers(pageNumber, pageSize, sortBy, sortDir), HttpStatus.OK);
    }

    // get single
    @GetMapping("/{userId}")
    @Operation(summary = "Get single user by user id !!")
    public ResponseEntity<UserDto> getUser(@PathVariable String userId) {
        return new ResponseEntity<>(userService.getUserById(userId), HttpStatus.OK);
    }

    // get by email
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        return new ResponseEntity<>(userService.getUserByEmail(email), HttpStatus.OK);
    }

    // search user
    @GetMapping("/search/{keywords}")
    public ResponseEntity<PageableResponse<UserDto>> searchUser(@PathVariable String keywords,
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "name", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir) {
        PageableResponse<UserDto> pageableResponse = userService.searchUser(keywords, pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(pageableResponse, HttpStatus.OK);
    }

    // upload user image
    @PostMapping("/image/{userId}")
    public ResponseEntity<ImageResponse> uploadUserImage(@RequestParam("userImage") MultipartFile image, @PathVariable String userId) throws IOException {
        String imageName = fileService.uploadFile(image, imageUploadPath);

        UserDto user = userService.getUserById(userId);
        user.setImageName(imageName);
        userService.updateUser(user, userId);

        ImageResponse imageResponse = ImageResponse.builder()
                                            .imageName(imageName)
                                            .message("Image is uploaded successfully")
                                            .success(true)
                                            .status(HttpStatus.CREATED)
                                        .build();
        return new ResponseEntity<>(imageResponse, HttpStatus.CREATED);
    }

    // serve user image
    @GetMapping("/image/{userId}")
    public void serveUserImage(@PathVariable String userId, HttpServletResponse response) throws IOException {
        UserDto user = userService.getUserById(userId);
        LOGGER.info("User image name : {}", user.getImageName());

        InputStream resource = fileService.getResource(imageUploadPath, user.getImageName());
        String extension = user.getImageName().substring(user.getImageName().lastIndexOf(".")+1);
        LOGGER.info("Extension : {}", extension);
        response.setContentType("image/"+extension);
        StreamUtils.copy(resource, response.getOutputStream());
    }
}
