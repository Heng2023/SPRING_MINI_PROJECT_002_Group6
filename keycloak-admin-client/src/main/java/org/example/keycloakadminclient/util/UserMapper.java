package org.example.keycloakadminclient.util;

import org.example.keycloakadminclient.model.User;
import org.example.keycloakadminclient.model.responsebody.UserResponse;

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
}

