package org.example.keycloakadminclient.service;
import jakarta.annotation.PostConstruct;
import org.example.keycloakadminclient.model.Group;
import org.example.keycloakadminclient.model.requestbody.GroupRequest;
import org.example.keycloakadminclient.model.responsebody.GroupResponse;
import org.example.keycloakadminclient.util.GroupMapper;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.GroupRepresentation;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import java.util.UUID;
import static org.example.keycloakadminclient.util.GroupMapper.mapToGroup;

@Service

public class GroupService {

//    @Value("${keycloak-admin.username}")
//    private String keycloakAdminUsername;
//
//    @Value("${keycloak-admin.password}")
//    private String keycloakAdminPassword;
//    private Keycloak buildKeycloakInstance() {
//        return KeycloakBuilder.builder()
//                .serverUrl(keycloakServerUrl)
//                .realm(keycloakRealm)
//                .clientId(keycloakResource)
//                .clientSecret(secretKey)
////                .username(keycloakAdminUsername)
////                .password(keycloakAdminPassword)
//                .build();
//    }
    //    @PostConstruct
//    public void init() {
//        keycloakAdminClient = KeycloakBuilder.builder()
//                .serverUrl(keycloakServerUrl)
//                .realm(keycloakRealm) // Consider using "keycloakRealm" if appropriate
//                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
//                .clientId(keycloakResource)
//                .clientSecret(secretKey)
////                .username(keycloakAdminUsername)
////                .password(keycloakAdminPassword)
//                .build();
//    }
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
//    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
//
//    @Value("${keycloak.realm}")
//    private String keycloakRealm;
//    private final Keycloak keycloakAdminClient;

    public GroupService(Keycloak keycloakAdminClient) {
        this.keycloakAdminClient = keycloakAdminClient;
    }

    public GroupResponse getGroupById(UUID groupId) {
        try {
            GroupRepresentation groupRepresentation = keycloakAdminClient.realm(keycloakRealm)
                    .groups().group(String.valueOf(groupId)).toRepresentation();

            if (groupRepresentation != null) {
                logger.info("Found group with ID: {}", groupId);
                Group group = mapToGroup(groupRepresentation);
                return GroupMapper.toGroupResponse(group);
            } else {
                logger.warn("Group with ID: {} not found", groupId);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error getting group by ID: {} in Keycloak", groupId, e);
            throw new RuntimeException("Failed getting group by ID in Keycloak", e);
        }
    }
    public GroupResponse updateGroupById(UUID groupId, GroupRequest updatedGroup ) {
        try {
            GroupRepresentation groupRepresentation = keycloakAdminClient.realm(keycloakRealm)
                    .groups().group(groupId.toString()).toRepresentation();
                groupRepresentation.setName(updatedGroup.getGroupName());
            groupRepresentation.setName(updatedGroup.getGroupName());
            keycloakAdminClient.realm(keycloakRealm).groups().group(String.valueOf(groupId)).update(groupRepresentation);
            logger.info("Updated group with ID: {}", groupId);
            Group group = mapToGroup(groupRepresentation);
            return GroupMapper.toGroupResponse(group);
        }
        catch (Exception e) {
            logger.error("Error updating group by ID: {} in Keycloak", groupId, e);
            throw new RuntimeException("Failed updating group by ID in Keycloak", e);
        }
    }
    public GroupResponse deleteGroupById(UUID groupId) {
        try {
            GroupRepresentation groupRepresentation = keycloakAdminClient.realm(keycloakRealm)
                   .groups().group(String.valueOf(groupId)).toRepresentation();
            if (groupRepresentation!= null) {
                keycloakAdminClient.realm(keycloakRealm).groups().group(String.valueOf(groupId)).remove();
                logger.info("Deleted group with ID: {}", groupId);
                Group group = mapToGroup(groupRepresentation);
                return GroupMapper.toGroupResponse(group);
            } else {
                logger.warn("Group with ID: {} not found", groupId);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error deleting group by ID: {} in Keycloak", groupId, e);
            throw new RuntimeException("Failed deleting group by ID in Keycloak", e);
        }
    }
}




