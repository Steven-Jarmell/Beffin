package com.beefin.services.controller;

import com.beefin.services.dto.UserRequest;
import com.beefin.services.dto.UserResponse;
import com.beefin.services.service.UserService;
import com.google.api.Http;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody UserRequest userRequest) throws ExecutionException, InterruptedException {
        if ( userRequest.getEmail() == null || userRequest.getPassword() == null || userRequest.getName() == null) {
            return new ResponseEntity<String>("All fields required", HttpStatus.BAD_REQUEST);
        }

        HttpStatus code = userService.createUser(userRequest);

        if (code == HttpStatus.CREATED) {
            return new ResponseEntity<String>("User Created", code);
        } else if (code == HttpStatus.CONFLICT) {
            return new ResponseEntity<String>("User already exists", code);
        } else {
            return new ResponseEntity<String>("Unexpected error occurred", code);
        }
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> getUsers(@RequestParam Optional<String> id) throws ExecutionException, InterruptedException {
        // If a GET request is made with an id provided, only get that user if they exist
        if (id.isPresent()) {
            List<UserResponse> user = userService.getUser(id.get());

            if (user == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist");
            } else {
                return user;
            }
        } else {
            List<UserResponse> allUsers = userService.getAllUsers();

            if (allUsers == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unexpected error occured while getting users");
            } else {
                return allUsers;
            }
        }
    }

    @PutMapping
    public ResponseEntity<String> updateUser(@RequestBody UserRequest userRequest) throws ExecutionException, InterruptedException {
        if (userRequest.getId() == null) {
            return new ResponseEntity<String>("Did not specify user ID in update", HttpStatus.BAD_REQUEST);
        }

        HttpStatus code = userService.update(userRequest);

        if (code == HttpStatus.OK) {
            return new ResponseEntity<String>("User was updated successfully", code);
        } else if (code == HttpStatus.BAD_REQUEST){
            return new ResponseEntity<String>("User either does not exist or there is an email conflict", code);
        } else {
            return new ResponseEntity<String>("Unexpected error occurred while updating", code);
        }
    }

    @DeleteMapping
    public ResponseEntity<String> deleteUser(@RequestParam String userID) throws ExecutionException, InterruptedException {
        if (userID == null) {
            return new ResponseEntity<String>("All userID required", HttpStatus.BAD_REQUEST);
        }

        HttpStatus code = userService.deleteUser(userID);

        if (code == HttpStatus.OK) {
            return new ResponseEntity<String>("User was deleted successfully", code);
        } else if (code == HttpStatus.BAD_REQUEST) {
            return new ResponseEntity<String>("The user to be deleted does not exist", code);
        } else {
            return new ResponseEntity<String>("Unexpected error occurred while deleting", code);
        }
    }
}
