package org.example.keycloakadminclient.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.example.keycloakadminclient.model.dto.requestbody.UserRequest;
import org.example.keycloakadminclient.model.dto.responsebody.ApiResponse;
import org.example.keycloakadminclient.model.dto.responsebody.UserResponse;
import org.example.keycloakadminclient.service.serviceImp.UserServiceImp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@SecurityRequirement(name = "spring-app")
public class UserController {

    private final UserServiceImp userServiceImp;

    public UserController(UserServiceImp userServiceImp) {
        this.userServiceImp = userServiceImp;
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserRequest userRequest) {
        ApiResponse<UserResponse> response = userServiceImp.createUser(
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
        ApiResponse<UserResponse> response = userServiceImp.getUserById(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        ApiResponse<List<UserResponse>> response = userServiceImp.getAllUsers();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/username")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByUsername(@RequestParam String username) {
        ApiResponse<UserResponse> response = userServiceImp.getUserByUsername(username);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/email")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(@RequestParam String email) {
        ApiResponse<UserResponse> response = userServiceImp.getUserByEmail(email);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteUser(@PathVariable UUID id) {
        ApiResponse<?> response = userServiceImp.deleteUser(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @Valid
            @PathVariable UUID id,
            @RequestBody UserRequest updatedUser) {
        ApiResponse<UserResponse> response = userServiceImp.updateUser(id, updatedUser);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
