package org.example.keycloakadminclient.model.responsebody;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GroupResponse {
   private UUID groupId;
   private String groupName;
}
