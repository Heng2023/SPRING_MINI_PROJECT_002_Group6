package org.example.keycloakadminclient.util;

import org.example.keycloakadminclient.model.User;
import org.example.keycloakadminclient.model.responsebody.UserResponse;
import org.keycloak.representations.idm.UserRepresentation;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
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
        // Map the createdAt timestamp
        LocalDateTime createdAt = userRepresentation.getCreatedTimestamp() != null
                ? LocalDateTime.ofInstant(Instant.ofEpochMilli(userRepresentation.getCreatedTimestamp()), ZoneId.systemDefault())
                : LocalDateTime.now();

        // Get the lastModified attribute from the custom attributes in Keycloak
        String lastModifiedStr = userRepresentation.getAttributes() != null
                ? userRepresentation.getAttributes().getOrDefault("lastModified", Collections.singletonList(null)).get(0)
                : null;

        // Convert the lastModified attribute to LocalDateTime, or set to null if not available
        LocalDateTime lastModified = lastModifiedStr != null ? LocalDateTime.parse(lastModifiedStr) : null;

        return new User(
                UUID.fromString(userRepresentation.getId()),
                userRepresentation.getUsername(),
                null, // Password should not be returned
                userRepresentation.getEmail(),
                userRepresentation.getFirstName(),
                userRepresentation.getLastName(),
                createdAt,
                lastModified  // Set the lastModified time properly
        );
    }
}

