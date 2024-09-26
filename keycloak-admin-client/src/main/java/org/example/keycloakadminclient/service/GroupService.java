package org.example.keycloakadminclient.service;

import jakarta.annotation.PostConstruct;
import org.example.keycloakadminclient.model.Group;
import org.example.keycloakadminclient.model.requestbody.GroupRequest;
import org.example.keycloakadminclient.model.responsebody.GroupResponse;
import org.example.keycloakadminclient.model.responsebody.UserGroupResponse;
import org.example.keycloakadminclient.model.responsebody.UserResponse;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
            groups.forEach(group -> logger.info("Group id: {}", group.getId()));
            groups.forEach(group -> logger.info("Group name: {}", group.getName()));

            Group group = new Group();
            for (GroupRepresentation group1 : groups) {
                if (group1.getName().equalsIgnoreCase(groupRequest.getGroupName())) {
                    group.setGroupId(UUID.fromString(group1.getId())); // Return the group's ID
                }
            }
            group.setGroupName(groupRequest.getGroupName());
            return group;
        } catch (Exception e) {
          throw new RuntimeException("Create group fail");
        }

    }

    // Get all group from keycloak
    public List<GroupResponse> getAllGroups() {
       try {
           RealmResource realmResource = keycloakAdminClient.realm(keycloakRealm);
           GroupsResource groupResource = realmResource.groups();

           List<GroupRepresentation> groups = groupResource.groups();
           logger.info("Fetch " + groups.size() + " groups from Keycloak");
           groups.forEach(group -> logger.info("Group: {}", group.getName()));

           return groups.stream()
                   .map(groupRepresentation -> {
                       GroupResponse groupResponse = new GroupResponse();
                       groupResponse.setGroupId(UUID.fromString(groupRepresentation.getId()));
                       groupResponse.setGroupName(groupRepresentation.getName());
                       return groupResponse;
                   }).toList();

       }catch (Exception e) {
           logger.error("Error getting groups in Keycloak", e);
           throw new RuntimeException("Failed getting groups in Keycloak", e);
       }
    }

    // Add user into group in keycloak
    public UserGroupResponse addUserToGroup(UUID groupId, UUID userId) {
        try {
            UsersResource usersResource = keycloakAdminClient.realm(keycloakRealm).users();
            UserResource userResource = usersResource.get(userId.toString());

            if (userResource == null) {
                throw new RuntimeException("User with Id: " + userId + " not found");
            }

            GroupRepresentation group = keycloakAdminClient.realm(keycloakRealm).groups().group(groupId.toString()).toRepresentation();

            if (group == null) {
                throw new RuntimeException("Group with Id: " + groupId + " not found");
            }

            // Add user to the group using groupId
            userResource.joinGroup(groupId.toString());
            logger.info("User with ID {} successfully added to group {} ", userId, group.getName());

            GroupResponse groups = new GroupResponse();
            groups.setGroupId(UUID.fromString(groupId.toString()));
            groups.setGroupName(group.getName());

            LocalDateTime created = LocalDateTime.ofInstant(Instant.ofEpochMilli(userResource.toRepresentation().getCreatedTimestamp()), ZoneId.systemDefault());

            UserResponse users = new UserResponse();
            users.setUserId(userId);
            users.setUsername(userResource.toRepresentation().getUsername());
            users.setFirstName(userResource.toRepresentation().getFirstName());
            users.setEmail(userResource.toRepresentation().getEmail());
            users.setLastName(userResource.toRepresentation().getLastName());
            users.setCreatedAt(created);
            UserGroupResponse response = new UserGroupResponse(users,groups);
            return response;

        } catch (Exception e) {
            logger.error("Error adding user id {} to group {} in Keycloak", userId, groupId, e);
            throw new RuntimeException("Failed creating group in Keycloak", e);
        }
    }

    // Get user by group ID from keycloak
    public GroupResponse getUserByGroupId(UUID groupId) {
        try {
            GroupResource groupResource = keycloakAdminClient.realm(keycloakRealm).groups().group(groupId.toString());

            List<UserRepresentation> users = groupResource.members();
            logger.info("Found {} users in group with id {} ", users.size(), groupId);

            List<UserResponse> userResponses = users.stream().map(userRepresentation -> {
                UserResponse user = new UserResponse();
                user.setUserId(UUID.fromString(userRepresentation.getId()));
                user.setUsername(userRepresentation.getUsername());
                user.setFirstName(userRepresentation.getFirstName());
                user.setLastName(userRepresentation.getLastName());
                user.setEmail(userRepresentation.getEmail());
                user.setCreatedAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(userRepresentation.getCreatedTimestamp()), ZoneId.systemDefault()));
                user.setLastModified(LocalDateTime.parse(userRepresentation.getAttributes().get("lastModified").getFirst()));
                return user;
            }).toList();

            GroupResponse groups = new GroupResponse();
            groups.setGroupId(groupId);
            groups.setGroupName(groupResource.toRepresentation().getName());
            groups.setUserList(userResponses);
            return groups;

        } catch (Exception e){
            logger.error("Error getting users in Keycloak", e);
            throw new RuntimeException("Failed getting users in Keycloak", e);
        }
    }


}
