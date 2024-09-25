package org.example.keycloakadminclient.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.example.keycloakadminclient.model.requestbody.GroupRequest;
import org.example.keycloakadminclient.model.responsebody.ApiResponse;
import org.example.keycloakadminclient.service.GroupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/group")
@SecurityRequirement(name = "spring-app")
public class GroupController {

    private final GroupService groupService;

    //create group
    @PostMapping
    public ResponseEntity<?> createGroup(@RequestBody GroupRequest groupRequest) {
        ApiResponse<Object> apiResponse = ApiResponse
                .builder()
                .message("Group created successfully")
                .payload(groupService.createGroup(groupRequest))
                .status(HttpStatus.CREATED)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    //get all groups
    @GetMapping
    public ResponseEntity<List<?>> getAllGroups() {
        ApiResponse<Object> apiResponse = ApiResponse
                .builder()
                .message("Get all groups successfully")
                .payload(groupService.getAllGroups())
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(Collections.singletonList(apiResponse));
    }

    // add user to group
    @PostMapping("/{groupId}/users/{userId}")
    public ResponseEntity<ApiResponse<Object>> addUserToGroup(@PathVariable UUID groupId, @PathVariable UUID userId ) {
        ApiResponse<Object> apiResponse = ApiResponse
                .builder()
                .message("Add user to group successfully")
                .payload(groupService.addUserToGroup(groupId, userId))
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    // find users by group id
    @GetMapping("/{groupId}/users")
    public ResponseEntity<List<?>> getAllUsersByGroups(@PathVariable UUID groupId) {
        ApiResponse<Object> apiResponse = ApiResponse
                .builder()
                .message("Get all users successfully")
                .payload(groupService.getUserByGroupId(groupId))
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(Collections.singletonList(apiResponse));
    }

}
