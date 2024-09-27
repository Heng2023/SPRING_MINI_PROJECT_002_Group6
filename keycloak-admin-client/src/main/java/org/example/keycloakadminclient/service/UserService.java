package org.example.keycloakadminclient.service;

import org.example.keycloakadminclient.model.dto.requestbody.UserRequest;
import org.example.keycloakadminclient.model.dto.responsebody.ApiResponse;
import org.example.keycloakadminclient.model.dto.responsebody.UserResponse;
import java.util.List;
import java.util.UUID;

public interface UserService {

    ApiResponse<UserResponse> createUser(String username, String email, String firstName, String lastName, String password);

    ApiResponse<UserResponse> getUserById(UUID userId);

    ApiResponse<List<UserResponse>> getAllUsers();

    ApiResponse<UserResponse> getUserByUsername(String username);

    ApiResponse<UserResponse> getUserByEmail(String email);

    ApiResponse<?> deleteUser(UUID userId);

    ApiResponse<UserResponse> updateUser(UUID userId, UserRequest updatedUser);
}
