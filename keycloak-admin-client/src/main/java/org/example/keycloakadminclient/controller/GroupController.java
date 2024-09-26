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
    public ResponseEntity<?> addUserToGroup(@PathVariable UUID groupId, @PathVariable UUID userId ) {
        return ResponseEntity.ok(new ApiResponse<>("Assign user " + userId +" to group " + groupId +" successfully "
                ,groupService.addUserToGroup(groupId,userId),HttpStatus.OK,LocalDateTime.now()));
    }

    // find users by group id
    @GetMapping("/{groupId}/users")
    public ResponseEntity<?> getAllUsersByGroups(@PathVariable UUID groupId) {
       return ResponseEntity.ok(new ApiResponse<>("Get user by group id " + groupId + " successfully",
               groupService.getUserByGroupId(groupId),HttpStatus.OK,LocalDateTime.now()));
    }

}
