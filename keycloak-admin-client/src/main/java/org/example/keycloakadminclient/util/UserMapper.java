package org.example.keycloakadminclient.util;

import org.example.keycloakadminclient.model.User;
import org.example.keycloakadminclient.model.responsebody.UserResponse;
import org.keycloak.representations.idm.UserRepresentation;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

public class UserMapper {
    public static UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getCreatedAt(),
                user.getLastModified()
        );
    }

    public static User mapToUser(UserRepresentation userRepresentation) {
        LocalDateTime createdAt = userRepresentation.getCreatedTimestamp() != null
                ? LocalDateTime.ofInstant(Instant.ofEpochMilli(userRepresentation.getCreatedTimestamp()), ZoneId.systemDefault())
                : LocalDateTime.now();

        return new User(
                UUID.fromString(userRepresentation.getId()),
                userRepresentation.getUsername(),
                null, // Password should not be returned
                userRepresentation.getEmail(),
                userRepresentation.getFirstName(),
                userRepresentation.getLastName(),
                createdAt,
                LocalDateTime.now() // Assume lastModified as now for simplicity
        );
    }
}

