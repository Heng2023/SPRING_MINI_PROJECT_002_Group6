package org.example.keycloakadminclient.service;

import jakarta.ws.rs.NotFoundException;
import org.example.keycloakadminclient.model.User;
import org.example.keycloakadminclient.model.requestbody.UserRequest;
import org.example.keycloakadminclient.model.responsebody.ApiResponse;
import org.example.keycloakadminclient.model.responsebody.UserResponse;
import org.example.keycloakadminclient.util.UserMapper;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.example.keycloakadminclient.util.UserMapper.mapToUser;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Value("${keycloak.resource}}")
    private String keycloakResource;

    @Value("${keycloak.credentials.secret}")
    private String secretKey;

//    @Value("${keycloak-admin.username}")
//    private String keycloakAdminUsername;
//
//    @Value("${keycloak-admin.password}")
//    private String keycloakAdminPassword;

    // Build Keycloak instance for reuse
    private Keycloak buildKeycloakInstance() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakServerUrl)
                .realm(keycloakResource)
                .clientId(secretKey)
//                .username(keycloakAdminUsername)
//                .password(keycloakAdminPassword)
                .build();
    }

    public ApiResponse<UserResponse> createUser(String username, String email, String firstName, String lastName, String password) {
        try (Keycloak keycloak = buildKeycloakInstance()) {

            UserRepresentation user = new UserRepresentation();
            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEnabled(true);

            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(password);
            credential.setTemporary(false);
            user.setCredentials(Collections.singletonList(credential));

            Response response = keycloak.realm(keycloakRealm).users().create(user);
            logger.info("Keycloak response status: {}", response.getStatus());

            if (response.getStatus() == 201) {
                UUID userId = UUID.randomUUID();
                LocalDateTime now = LocalDateTime.now();
                User newUser = new User(userId, username, null, email, firstName, lastName, now, now);
                UserResponse userResponse = UserMapper.toUserResponse(newUser);

                return new ApiResponse<>("User created successfully", userResponse, HttpStatus.CREATED, LocalDateTime.now());
            } else {
                logger.error("Failed to create user: {}", response.readEntity(String.class));
                return new ApiResponse<>("Failed to create user", null, HttpStatus.valueOf(response.getStatus()), LocalDateTime.now());
            }
        }
    }

    public ApiResponse<UserResponse> getUserById(UUID userId) {
        try (Keycloak keycloak = buildKeycloakInstance()) {
            UserRepresentation userRepresentation = keycloak.realm(keycloakRealm).users().get(userId.toString()).toRepresentation();

            if (userRepresentation == null) {
                return new ApiResponse<>("User not found", null, HttpStatus.NOT_FOUND, LocalDateTime.now());
            }

            User user = mapToUser(userRepresentation);
            UserResponse userResponse = UserMapper.toUserResponse(user);
            return new ApiResponse<>("User found successfully", userResponse, HttpStatus.OK, LocalDateTime.now());

        } catch (NotFoundException e) {
            return new ApiResponse<>("User not found", null, HttpStatus.NOT_FOUND, LocalDateTime.now());
        }
    }

    public ApiResponse<List<UserResponse>> getAllUsers() {
        try (Keycloak keycloak = buildKeycloakInstance()) {
            List<UserRepresentation> users = keycloak.realm(keycloakRealm).users().list();

            List<UserResponse> userResponses = users.stream()
                    .map(UserMapper::mapToUser)
                    .map(UserMapper::toUserResponse)
                    .collect(Collectors.toList());

            return new ApiResponse<>("Users retrieved successfully", userResponses, HttpStatus.OK, LocalDateTime.now());
        }
    }

    public ApiResponse<UserResponse> getUserByUsername(String username) {
        return searchUser(username, null);
    }

    public ApiResponse<UserResponse> getUserByEmail(String email) {
        return searchUser(null, email);
    }

    public ApiResponse<?> deleteUser(UUID userId) {
        try (Keycloak keycloak = buildKeycloakInstance()) {
            keycloak.realm(keycloakRealm).users().get(userId.toString()).remove();
            return new ApiResponse<>("User deleted successfully", null, HttpStatus.OK, LocalDateTime.now());
        } catch (NotFoundException e) {
            logger.error("User not found: {}", e.getMessage());
            return new ApiResponse<>("User not found", null, HttpStatus.NOT_FOUND, LocalDateTime.now());
        }
    }

    public ApiResponse<UserResponse> updateUser(UUID userId, UserRequest updatedUser) {
        try (Keycloak keycloak = buildKeycloakInstance()) {
            UserResource userResource = keycloak.realm(keycloakRealm).users().get(userId.toString());
            UserRepresentation userRepresentation = userResource.toRepresentation();

            if (userRepresentation == null) {
                return new ApiResponse<>("User not found", null, HttpStatus.NOT_FOUND, LocalDateTime.now());
            }

            updateUserDetails(userRepresentation, updatedUser);
            userResource.update(userRepresentation);

            User user = mapToUser(userRepresentation);
            UserResponse userResponse = UserMapper.toUserResponse(user);

            return new ApiResponse<>("User updated successfully", userResponse, HttpStatus.OK, LocalDateTime.now());

        } catch (NotFoundException e) {
            logger.error("User not found: {}", e.getMessage());
            return new ApiResponse<>("User not found", null, HttpStatus.NOT_FOUND, LocalDateTime.now());
        } catch (Exception e) {
            logger.error("Error updating user: {}", e.getMessage());
            return new ApiResponse<>("Error updating user", null, HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now());
        }
    }

    private ApiResponse<UserResponse> searchUser(String username, String email) {
        try (Keycloak keycloak = buildKeycloakInstance()) {
            List<UserRepresentation> users = keycloak.realm(keycloakRealm).users().search(username, null, null, email, 0, 1);

            if (users == null || users.isEmpty()) {
                return new ApiResponse<>("User not found", null, HttpStatus.NOT_FOUND, LocalDateTime.now());
            }

            User user = mapToUser(users.get(0));
            UserResponse userResponse = UserMapper.toUserResponse(user);
            return new ApiResponse<>("User found successfully", userResponse, HttpStatus.OK, LocalDateTime.now());

        } catch (Exception e) {
            logger.error("Error fetching user: {}", e.getMessage());
            return new ApiResponse<>("Error fetching user", null, HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now());
        }
    }

    private void updateUserDetails(UserRepresentation userRepresentation, UserRequest updatedUser) {
        userRepresentation.setUsername(updatedUser.getUsername());
        userRepresentation.setEmail(updatedUser.getEmail());
        userRepresentation.setFirstName(updatedUser.getFirstName());
        userRepresentation.setLastName(updatedUser.getLastName());
    }
}
