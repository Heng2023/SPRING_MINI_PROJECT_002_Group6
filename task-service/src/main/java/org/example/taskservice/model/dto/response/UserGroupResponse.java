package org.example.taskservice.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UserGroupResponse {
    private UUID groupId;
    private String groupName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<UserResponse> userList;
}
