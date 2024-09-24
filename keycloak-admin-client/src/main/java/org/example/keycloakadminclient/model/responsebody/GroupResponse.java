package org.example.keycloakadminclient.model.responsebody;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.keycloak.representations.account.UserRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;

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
