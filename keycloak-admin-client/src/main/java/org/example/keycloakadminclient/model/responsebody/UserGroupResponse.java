package org.example.keycloakadminclient.model.responsebody;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserGroupResponse {
    private UserResponse user;
    private GroupResponse group;
}
