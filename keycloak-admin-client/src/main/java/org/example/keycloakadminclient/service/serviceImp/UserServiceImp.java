package org.example.keycloakadminclient.service.serviceImp;

import org.example.keycloakadminclient.model.User;
import org.example.keycloakadminclient.model.dto.requestbody.UserRequest;
import org.example.keycloakadminclient.model.dto.responsebody.ApiResponse;
import org.example.keycloakadminclient.model.dto.responsebody.UserResponse;
import org.example.keycloakadminclient.service.UserService;
import org.example.keycloakadminclient.util.UserMapper;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
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
public class UserServiceImp implements UserService {

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Value("${keycloak-admin.username}")
    private String keycloakAdminUsername;

    @Value("${keycloak-admin.password}")
    private String keycloakAdminPassword;

    private Keycloak buildKeycloakInstance() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakServerUrl)
                .realm("master")
                .clientId("admin-cli")
                .username(keycloakAdminUsername)
                .password(keycloakAdminPassword)
                .build();
    }

    @Override
    public ApiResponse<UserResponse> createUser(String username, String email, String firstName, String lastName, String password) {
        Keycloak keycloak = buildKeycloakInstance();

        // Prepare the UserRepresentation with all required fields
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);

        // Create credentials representation
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        user.setCredentials(Collections.singletonList(credential));

        // Set createdAt and lastModified to current time
        LocalDateTime now = LocalDateTime.now();
        user.singleAttribute("lastModified", now.toString());  // Custom attribute for lastModified

        // Create the user in Keycloak
        Response response = keycloak.realm(keycloakRealm).users().create(user);

        // Handle the response and extract userId
        if (response.getStatus() == 201) {
            String locationHeader = response.getHeaderString("Location");
            String userId = extractUserIdFromLocation(locationHeader);

            // Map the created user details to the domain model
            User newUser = new User(
                    UUID.fromString(userId),
                    username,
                    null,  // Do not return the password
                    email,
                    firstName,
                    lastName,
                    now,  // Set createdAt
                    now   // Set lastModified (same as createdAt in this case)
            );
            UserResponse userResponse = UserMapper.toUserResponse(newUser);

            return new ApiResponse<>("User created successfully", userResponse, HttpStatus.CREATED, now);
        } else {
            throw new IllegalStateException("Failed to create user: " + response.getStatusInfo());
        }
    }

    @Override
    public ApiResponse<UserResponse> getUserById(UUID userId) {
        Keycloak keycloak = buildKeycloakInstance();
        UserRepresentation userRepresentation = keycloak.realm(keycloakRealm).users().get(userId.toString()).toRepresentation();

        if (userRepresentation == null) {
            throw new IllegalArgumentException("User not found");
        }

        User user = mapToUser(userRepresentation);
        UserResponse userResponse = UserMapper.toUserResponse(user);
        return new ApiResponse<>("User found successfully", userResponse, HttpStatus.OK, LocalDateTime.now());
    }

    @Override
    public ApiResponse<List<UserResponse>> getAllUsers() {
        Keycloak keycloak = buildKeycloakInstance();
        List<UserRepresentation> users = keycloak.realm(keycloakRealm).users().list();

        List<UserResponse> userResponses = users.stream()
                .map(UserMapper::mapToUser)
                .map(UserMapper::toUserResponse)
                .collect(Collectors.toList());

        return new ApiResponse<>("Users retrieved successfully", userResponses, HttpStatus.OK, LocalDateTime.now());
    }

    @Override
    public ApiResponse<UserResponse> getUserByUsername(String username) {
        return searchUser(username, null);
    }

    @Override
    public ApiResponse<UserResponse> getUserByEmail(String email) {
        return searchUser(null, email);
    }

    @Override
    public ApiResponse<?> deleteUser(UUID userId) {
        Keycloak keycloak = buildKeycloakInstance();
        keycloak.realm(keycloakRealm).users().get(userId.toString()).remove();
        return new ApiResponse<>("User deleted successfully", null, HttpStatus.OK, LocalDateTime.now());
    }

    @Override
    public ApiResponse<UserResponse> updateUser(UUID userId, UserRequest updatedUser) {
        Keycloak keycloak = buildKeycloakInstance();
        UserResource userResource = keycloak.realm(keycloakRealm).users().get(userId.toString());
        UserRepresentation userRepresentation = userResource.toRepresentation();

        if (userRepresentation == null) {
            throw new IllegalArgumentException("User not found");
        }

        updateUserDetails(userRepresentation, updatedUser);

        LocalDateTime now = LocalDateTime.now();
        userRepresentation.singleAttribute("lastModified", now.toString());
        userResource.update(userRepresentation);

        User user = mapToUser(userRepresentation);
        user.setLastModified(now);

        UserResponse userResponse = UserMapper.toUserResponse(user);
        return new ApiResponse<>("User updated successfully", userResponse, HttpStatus.OK, now);
    }

    private ApiResponse<UserResponse> searchUser(String username, String email) {
        Keycloak keycloak = buildKeycloakInstance();
        List<UserRepresentation> users = keycloak.realm(keycloakRealm).users().search(username, null, null, email, 0, 1);

        if (users == null || users.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = mapToUser(users.get(0));
        UserResponse userResponse = UserMapper.toUserResponse(user);
        return new ApiResponse<>("User found successfully", userResponse, HttpStatus.OK, LocalDateTime.now());
    }

    private String extractUserIdFromLocation(String locationHeader) {
        if (locationHeader == null) {
            throw new IllegalStateException("Location header is missing in the Keycloak response");
        }
        return locationHeader.substring(locationHeader.lastIndexOf("/") + 1);
    }

    private void updateUserDetails(UserRepresentation userRepresentation, UserRequest updatedUser) {
        userRepresentation.setUsername(updatedUser.getUsername());
        userRepresentation.setEmail(updatedUser.getEmail());
        userRepresentation.setFirstName(updatedUser.getFirstName());
        userRepresentation.setLastName(updatedUser.getLastName());
    }
}
