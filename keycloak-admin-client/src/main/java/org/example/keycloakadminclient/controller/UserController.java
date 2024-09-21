package org.example.keycloakadminclient.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.example.keycloakadminclient.model.requestbody.UserRequest;
import org.example.keycloakadminclient.model.responsebody.ApiResponse;
import org.example.keycloakadminclient.model.responsebody.UserResponse;
import org.example.keycloakadminclient.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@SecurityRequirement(name = "spring-app")
public class UserController {

     private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody UserRequest userRequest) {
        ApiResponse<UserResponse> response = userService.createUser(
                userRequest.getUsername(),
                userRequest.getEmail(),
                userRequest.getFirstName(),
                userRequest.getLastName(),
                userRequest.getPassword()
        );

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        ApiResponse<UserResponse> response = userService.getUserById(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        ApiResponse<List<UserResponse>> response = userService.getAllUsers();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/username")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByUsername(@RequestParam String username) {
        ApiResponse<UserResponse> response = userService.getUserByUsername(username);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/email")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(@RequestParam String email) {
        ApiResponse<UserResponse> response = userService.getUserByEmail(email);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteUser(@PathVariable UUID id) {
        ApiResponse<?> response = userService.deleteUser(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable UUID id,
            @RequestBody UserRequest updatedUser) {
        ApiResponse<UserResponse> response = userService.updateUser(id, updatedUser);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
