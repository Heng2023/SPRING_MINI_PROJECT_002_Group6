package org.example.keycloakadminclient.util;
import org.example.keycloakadminclient.model.Group;
import org.example.keycloakadminclient.model.dto.responsebody.GroupResponse;
import org.keycloak.representations.idm.GroupRepresentation;
import java.util.UUID;

public class GroupMapper {
    public static GroupResponse toGroupResponse(Group group) {
        return new GroupResponse(
                group.getGroupId(),
                group.getGroupName(),
                null
        );
    }

    public static Group mapToGroup(GroupRepresentation groupRepresentation) {
        System.out.println(groupRepresentation.getName());
        return new Group(
                UUID.fromString(groupRepresentation.getId()),
                groupRepresentation.getName()
        );
    }
}