package org.example.keycloakadminclient.model.dto.requestbody;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupRequest {
    private String groupName;
}
