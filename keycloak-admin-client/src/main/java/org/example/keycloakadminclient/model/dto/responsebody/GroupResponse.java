package org.example.keycloakadminclient.model.dto.responsebody;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GroupResponse {
   private UUID groupId;
   private String groupName;
   @JsonInclude(JsonInclude.Include.NON_NULL)
   private List<UserResponse> userList;
}
