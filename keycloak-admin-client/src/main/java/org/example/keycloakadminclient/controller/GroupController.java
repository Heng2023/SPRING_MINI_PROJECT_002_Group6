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
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/groups")
@SecurityRequirement(name = "spring-app")
public class GroupController {
    private final GroupService groupService;


    @GetMapping("/{groupId}")
    public ResponseEntity<?> getGroupById(@PathVariable UUID groupId) {
        ApiResponse<Object> response = ApiResponse
                .builder()
                .message("Get group by id successful")
                .payload(groupService.getGroupById(groupId))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok(response);
    }
    @PutMapping("/{groupId}")
    public ResponseEntity<?> updateGroupById(@PathVariable UUID groupId, @RequestBody GroupRequest updatedGroup) {

        ApiResponse<Object> response = ApiResponse
                .builder()
                .message("Update group by id successful")
                .payload(groupService.updateGroupById(groupId, updatedGroup))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{groupId}")
    public ResponseEntity<?> deleteGroupById(@PathVariable UUID groupId) {
        ApiResponse<Object> response = ApiResponse
                .builder()
                .message("Delete group by id successful")
                .payload(groupService.deleteGroupById(groupId))
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

}
