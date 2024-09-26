package org.example.keycloakadminclient.service;

import jakarta.annotation.PostConstruct;
import org.example.keycloakadminclient.model.Group;
import org.example.keycloakadminclient.model.requestbody.GroupRequest;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GroupService {

    private static final Logger logger = LoggerFactory.getLogger(GroupService.class);

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Value("${keycloak-admin.username}")
    private String keycloakAdminUsername;

    @Value("${keycloak-admin.password}")
    private String keycloakAdminPassword;

    private Keycloak keycloakAdminClient;

    @PostConstruct
    public void init() {
        keycloakAdminClient = KeycloakBuilder.builder()
                .serverUrl(keycloakServerUrl)
                .realm("master")
                .grantType(OAuth2Constants.PASSWORD)
                .clientId("admin-cli")
                .username(keycloakAdminUsername)
                .password(keycloakAdminPassword)
                .build();
    }

    // Create Group in Keycloak
    public Group createGroup(GroupRequest groupRequest) {
        GroupRepresentation groupRepresentation = new GroupRepresentation();
        groupRepresentation.setName(groupRequest.getGroupName());

        try {
            keycloakAdminClient.realm(keycloakRealm).groups().add(groupRepresentation);
            logger.info("Created group: " + groupRequest.getGroupName());

            // Debugging: Fetch all groups to verify
            List<GroupRepresentation> groups = keycloakAdminClient.realm(keycloakRealm).groups().groups();
            groups.forEach(group -> logger.info("Group: {}", group.getId()));
            groups.forEach(group -> logger.info("Group: {}", group.getName()));

        } catch (Exception e) {
            logger.error("Error creating group in Keycloak", e);
        }

        Group group = new Group();
        group.setGroupName(groupRequest.getGroupName());
        return group;
    }

    // Get all group from keycloak
    public List<GroupRepresentation> getAllGroups() {
       try {
           RealmResource realmResource = keycloakAdminClient.realm(keycloakRealm);
           GroupsResource groupResource = realmResource.groups();

           List<GroupRepresentation> groups = groupResource.groups();
           logger.info("Fetch " + groups.size() + " groups from Keycloak");
           groups.forEach(group -> logger.info("Group: {}", group.getName()));
           return groups;
       }catch (Exception e) {
           logger.error("Error getting groups in Keycloak", e);
           throw new RuntimeException("Failed getting groups in Keycloak", e);
       }
    }

    // Add user into group in keycloak
    public GroupRepresentation addUserToGroup(UUID groupId, UUID userId) {
        try {
            UsersResource usersResource = keycloakAdminClient.realm(keycloakRealm).users();
            UserResource userResource = usersResource.get(String.valueOf(userId));

            if (userResource == null) {
                throw new RuntimeException("User with Id: " + userId + " not found");
            }

            GroupRepresentation group = keycloakAdminClient.realm(keycloakRealm).groups().group(String.valueOf(groupId)).toRepresentation();

            if (group == null) {
                throw new RuntimeException("Group with Id: " + groupId + " not found");
            }

            // Add user to the group using groupId
            userResource.joinGroup(String.valueOf(groupId));
            logger.info("User with ID {} successfully added to group {} ", userId, group.getName());

            Group groups = new Group();
            groups.setGroupId(UUID.fromString(String.valueOf(groupId)));
            return group;

        } catch (Exception e) {
            logger.error("Error adding user id {} to group {} in Keycloak", userId, groupId, e);
            throw new RuntimeException("Failed creating group in Keycloak", e);
        }
    }

    // Get user by group ID from keycloak
    public List<UserRepresentation> getUserByGroupId(UUID groupId) {
        try {
            GroupResource groupResource = (GroupResource) keycloakAdminClient.realm(keycloakRealm).groups().group(groupId.toString());

            List<UserRepresentation> users = groupResource.members();
            logger.info("Found {} users in group with id {} ", users.size(), groupId);
            return users;

        } catch (Exception e){
            logger.error("Error getting users in Keycloak", e);
            throw new RuntimeException("Failed getting users in Keycloak", e);
        }
    }


}
