package org.example.keycloakadminclient.model.requestbody;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupRequest {
    private String groupName;
}
