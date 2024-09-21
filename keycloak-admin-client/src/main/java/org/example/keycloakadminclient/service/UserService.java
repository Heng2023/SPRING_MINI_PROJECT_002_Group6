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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Value("${keycloak-admin.username}")
    private String keycloakAdminUsername;

    @Value("${keycloak-admin.password}")
    private String keycloakAdminPassword;

    public ApiResponse<UserResponse> createUser(String username, String email, String firstName, String lastName, String password) {
        Keycloak keycloak = null;
        try {
            keycloak = KeycloakBuilder.builder()
                    .serverUrl(keycloakServerUrl)
                    .realm("master") // Use master realm to authenticate as admin
                    .clientId("admin-cli")
                    .username(keycloakAdminUsername)
                    .password(keycloakAdminPassword)
                    .build();

            // Create a new user representation
            UserRepresentation user = new UserRepresentation();
            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEnabled(true);

            // Set credentials (password)
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(password);
            credential.setTemporary(false);

            user.setCredentials(Collections.singletonList(credential));

            // Create the user in Keycloak
            Response response = keycloak.realm(keycloakRealm).users().create(user);

            logger.info("Keycloak response status: {}", response.getStatus());
            if (response.getStatus() == 201) {
                // Assuming the user is successfully created, you can create a `User` object
                UUID userId = UUID.randomUUID(); // Generate or retrieve this UUID from Keycloak if possible
                LocalDateTime createdAt = LocalDateTime.now();
                User newUser = new User(
                        userId,
                        username,
                        password, // In a real-world scenario, you shouldn't return the password directly
                        email,
                        firstName,
                        lastName,
                        createdAt,
                        createdAt // lastModified initially the same as createdAt
                );

                // Map the User object to a UserResponse
                UserResponse userResponse = UserMapper.toUserResponse(newUser);

                // Return the ApiResponse<UserResponse>
                return new ApiResponse<>(
                        "User created successfully",
                        userResponse,
                        HttpStatus.CREATED,
                        LocalDateTime.now()
                );
            } else {
                logger.error("Failed to create user: {}", response.readEntity(String.class));
                return new ApiResponse<>(
                        "Failed to create user",
                        null,
                        HttpStatus.valueOf(response.getStatus()),
                        LocalDateTime.now()
                );
            }
        } finally {
            if (keycloak != null) {
                keycloak.close();
            }
        }
    }

     public ApiResponse<UserResponse> getUserById(UUID userId) {
        Keycloak keycloak = null;
        try {
            keycloak = KeycloakBuilder.builder()
                    .serverUrl(keycloakServerUrl)
                    .realm("master")  // Use master realm to authenticate as admin
                    .clientId("admin-cli")
                    .username(keycloakAdminUsername)
                    .password(keycloakAdminPassword)
                    .build();

            // Fetch user from Keycloak using the userId
            UserRepresentation userRepresentation = keycloak.realm(keycloakRealm).users().get(userId.toString()).toRepresentation();

            if (userRepresentation == null) {
                return new ApiResponse<>(
                        "User not found",
                        null,
                        HttpStatus.NOT_FOUND,
                        LocalDateTime.now()
                );
            }

            // Create a User object
            User user = new User(
                    UUID.fromString(userRepresentation.getId()),
                    userRepresentation.getUsername(),
                    null, // Password should not be returned
                    userRepresentation.getEmail(),
                    userRepresentation.getFirstName(),
                    userRepresentation.getLastName(),
                    LocalDateTime.now(), // Set createdAt with default value for now (depends on real creation timestamp)
                    LocalDateTime.now()  // Set lastModified with default value
            );

            // Convert User to UserResponse
            UserResponse userResponse = UserMapper.toUserResponse(user);

            // Return the ApiResponse with the UserResponse
            return new ApiResponse<>(
                    "User found successfully",
                    userResponse,
                    HttpStatus.OK,
                    LocalDateTime.now()
            );
        } catch (NotFoundException e) {
            return new ApiResponse<>(
                    "User not found",
                    null,
                    HttpStatus.NOT_FOUND,
                    LocalDateTime.now()
            );
        } finally {
            if (keycloak != null) {
                keycloak.close();
            }
        }
    }

    public ApiResponse<List<UserResponse>> getAllUsers() {
        Keycloak keycloak = null;
        try {
            keycloak = KeycloakBuilder.builder()
                    .serverUrl(keycloakServerUrl)
                    .realm("master")  // Use master realm to authenticate as admin
                    .clientId("admin-cli")
                    .username(keycloakAdminUsername)
                    .password(keycloakAdminPassword)
                    .build();

            // Fetch all users from Keycloak
            List<UserRepresentation> userRepresentations = keycloak.realm(keycloakRealm).users().list();

            // Convert each UserRepresentation to UserResponse using UserMapper
            List<UserResponse> userResponses = userRepresentations.stream().map(userRepresentation -> {
                User user = new User(
                        UUID.fromString(userRepresentation.getId()),
                        userRepresentation.getUsername(),
                        null, // Password should not be returned
                        userRepresentation.getEmail(),
                        userRepresentation.getFirstName(),
                        userRepresentation.getLastName(),
                        LocalDateTime.now(), // Set createdAt with default value for now
                        LocalDateTime.now()  // Set lastModified with default value
                );
                return UserMapper.toUserResponse(user);
            }).collect(Collectors.toList());

            // Return the ApiResponse with the list of UserResponse
            return new ApiResponse<>(
                    "Users retrieved successfully",
                    userResponses,
                    HttpStatus.OK,
                    LocalDateTime.now()
            );
        } finally {
            if (keycloak != null) {
                keycloak.close();
            }
        }
    }

     public ApiResponse<UserResponse> getUserByUsername(String username) {
        Keycloak keycloak = null;
        try {
            keycloak = KeycloakBuilder.builder()
                    .serverUrl(keycloakServerUrl)
                    .realm("master")  // Use master realm to authenticate as admin
                    .clientId("admin-cli")
                    .username(keycloakAdminUsername)
                    .password(keycloakAdminPassword)
                    .build();

            // Fetch users by username from Keycloak (Keycloak returns a list of users)
            List<UserRepresentation> users = keycloak.realm(keycloakRealm).users().search(username, true);

            if (users == null || users.isEmpty()) {
                return new ApiResponse<>(
                        "User not found",
                        null,
                        HttpStatus.NOT_FOUND,
                        LocalDateTime.now()
                );
            }

            UserRepresentation userRepresentation = users.get(0); // Assuming the first match is the correct one

            // Create a User object
            User user = new User(
                    UUID.fromString(userRepresentation.getId()),
                    userRepresentation.getUsername(),
                    null, // Password should not be returned
                    userRepresentation.getEmail(),
                    userRepresentation.getFirstName(),
                    userRepresentation.getLastName(),
                    LocalDateTime.now(), // Set createdAt with default value for now
                    LocalDateTime.now()  // Set lastModified with default value
            );

            // Convert User to UserResponse
            UserResponse userResponse = UserMapper.toUserResponse(user);

            // Return the ApiResponse with the UserResponse
            return new ApiResponse<>(
                    "User found successfully",
                    userResponse,
                    HttpStatus.OK,
                    LocalDateTime.now()
            );

        } catch (Exception e) {
            logger.error("Error fetching user by username: {}", e.getMessage());
            return new ApiResponse<>(
                    "Error fetching user",
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    LocalDateTime.now()
            );
        } finally {
            if (keycloak != null) {
                keycloak.close();
            }
        }
    }

    public ApiResponse<UserResponse> getUserByEmail(String email) {
        Keycloak keycloak = null;
        try {
            keycloak = KeycloakBuilder.builder()
                    .serverUrl(keycloakServerUrl)
                    .realm("master")  // Use master realm to authenticate as admin
                    .clientId("admin-cli")
                    .username(keycloakAdminUsername)
                    .password(keycloakAdminPassword)
                    .build();

            // Fetch users by email from Keycloak (Keycloak returns a list of users)
            List<UserRepresentation> users = keycloak.realm(keycloakRealm).users().search(null, null, null, email, 0, 1);

            if (users == null || users.isEmpty()) {
                return new ApiResponse<>(
                        "User not found",
                        null,
                        HttpStatus.NOT_FOUND,
                        LocalDateTime.now()
                );
            }

            UserRepresentation userRepresentation = users.get(0); // Assuming the first match is the correct one

            // Create a User object
            User user = new User(
                    UUID.fromString(userRepresentation.getId()),
                    userRepresentation.getUsername(),
                    null, // Password should not be returned
                    userRepresentation.getEmail(),
                    userRepresentation.getFirstName(),
                    userRepresentation.getLastName(),
                    LocalDateTime.now(), // Set createdAt with default value for now
                    LocalDateTime.now()  // Set lastModified with default value
            );

            // Convert User to UserResponse
            UserResponse userResponse = UserMapper.toUserResponse(user);

            // Return the ApiResponse with the UserResponse
            return new ApiResponse<>(
                    "User found successfully",
                    userResponse,
                    HttpStatus.OK,
                    LocalDateTime.now()
            );

        } catch (Exception e) {
            logger.error("Error fetching user by email: {}", e.getMessage());
            return new ApiResponse<>(
                    "Error fetching user",
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    LocalDateTime.now()
            );
        } finally {
            if (keycloak != null) {
                keycloak.close();
            }
        }
    }

    public ApiResponse<?> deleteUser(UUID userId) {
        Keycloak keycloak = null;
        try {
            keycloak = KeycloakBuilder.builder()
                    .serverUrl(keycloakServerUrl)
                    .realm("master")  // Use master realm to authenticate as admin
                    .clientId("admin-cli")
                    .username(keycloakAdminUsername)
                    .password(keycloakAdminPassword)
                    .build();

            // Attempt to delete the user by ID
            keycloak.realm(keycloakRealm).users().get(userId.toString()).remove();

            // If successful, return a success response
            return new ApiResponse<>(
                    "User deleted successfully",
                    null, // No payload is needed for delete
                    HttpStatus.NO_CONTENT, // HTTP 204 for successful deletion
                    LocalDateTime.now()
            );

        } catch (NotFoundException e) {
            // Handle case where the user does not exist
            logger.error("User not found: {}", e.getMessage());
            return new ApiResponse<>(
                    "User not found",
                    null,
                    HttpStatus.NOT_FOUND,
                    LocalDateTime.now()
            );
        } catch (Exception e) {
            // Handle other exceptions
            logger.error("Error deleting user: {}", e.getMessage());
            return new ApiResponse<>(
                    "Error deleting user",
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    LocalDateTime.now()
            );
        } finally {
            if (keycloak != null) {
                keycloak.close();
            }
        }
    }

    public ApiResponse<UserResponse> updateUser(UUID userId, UserRequest updatedUser) {
        Keycloak keycloak = null;
        try {
            keycloak = KeycloakBuilder.builder()
                    .serverUrl(keycloakServerUrl)
                    .realm("master")  // Use master realm to authenticate as admin
                    .clientId("admin-cli")
                    .username(keycloakAdminUsername)
                    .password(keycloakAdminPassword)
                    .build();

            // Retrieve the UserResource
            UserResource userResource = keycloak.realm(keycloakRealm).users().get(userId.toString());

            // Fetch the user representation
            UserRepresentation userRepresentation = userResource.toRepresentation();
            if (userRepresentation == null) {
                return new ApiResponse<>(
                        "User not found",
                        null,
                        HttpStatus.NOT_FOUND,
                        LocalDateTime.now()
                );
            }

            // Log the current user details before updating
            logger.info("Existing User Details: {}", userRepresentation);

            // Update user attributes
            userRepresentation.setUsername(updatedUser.getUsername());
            userRepresentation.setEmail(updatedUser.getEmail());
            userRepresentation.setFirstName(updatedUser.getFirstName());
            userRepresentation.setLastName(updatedUser.getLastName());

            // Perform the update
            userResource.update(userRepresentation);

            // Log the updated user details
            logger.info("Updated User Details: {}", userRepresentation);

            // Capture the current time for lastModified
            LocalDateTime now = LocalDateTime.now();

            // Create updated User object
            User user = new User(
                    userId,
                    updatedUser.getUsername(),
                    null, // Do not return the password
                    updatedUser.getEmail(),
                    updatedUser.getFirstName(),
                    updatedUser.getLastName(),
                    userRepresentation.getCreatedTimestamp() != null ?
                        LocalDateTime.ofInstant(Instant.ofEpochMilli(userRepresentation.getCreatedTimestamp()), ZoneId.systemDefault()) :
                        now, // Fallback for createdAt
                    now // lastModified should always be updated to the current time
            );

            // Convert updated User to UserResponse
            UserResponse userResponse = UserMapper.toUserResponse(user);

            // Return success response with the updated user
            return new ApiResponse<>(
                    "User updated successfully",
                    userResponse,
                    HttpStatus.OK,
                    LocalDateTime.now()
            );

        } catch (javax.ws.rs.NotFoundException e) {
            logger.error("User not found: {}", e.getMessage());
            return new ApiResponse<>(
                    "User not found",
                    null,
                    HttpStatus.NOT_FOUND,
                    LocalDateTime.now()
            );
        } catch (javax.ws.rs.ClientErrorException e) {
            logger.error("Error updating user: {}", e.getResponse().readEntity(String.class));
            return new ApiResponse<>(
                    "Error updating user: " + e.getResponse().readEntity(String.class),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    LocalDateTime.now()
            );
        } catch (Exception e) {
            logger.error("Error updating user: {}", e.getMessage());
            return new ApiResponse<>(
                    "Error updating user",
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    LocalDateTime.now()
            );
        } finally {
            if (keycloak != null) {
                keycloak.close();
            }
        }
    }
}
