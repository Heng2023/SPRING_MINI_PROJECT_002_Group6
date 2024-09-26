package org.example.keycloakadminclient.model;

import lombok.*;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Group {
    private UUID groupId;
    private String groupName;
}
