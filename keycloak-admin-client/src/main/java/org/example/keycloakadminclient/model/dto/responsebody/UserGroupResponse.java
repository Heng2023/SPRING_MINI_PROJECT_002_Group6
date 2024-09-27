package org.example.keycloakadminclient.model.dto.responsebody;

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
