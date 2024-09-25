package org.example.taskservice.service.serviceImp;

import lombok.AllArgsConstructor;

import org.example.taskservice.model.dto.request.TaskRequest;
import org.example.taskservice.model.dto.response.GroupResponse;
import org.example.taskservice.model.dto.response.TaskResponse;
import org.example.taskservice.model.dto.response.UserResponse;
import org.example.taskservice.model.entity.Task;
import org.example.taskservice.repository.TaskRepository;
import org.example.taskservice.service.TaskService;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
public class TaskServiceImp implements TaskService {

    @Value("${keycloak.realm}")
    private String realm;
    private final Keycloak keycloak;
    private final TaskRepository taskRepository;

    public TaskServiceImp(TaskRepository taskRepository, Keycloak keycloak) {
        this.taskRepository = taskRepository;
        this.keycloak = keycloak;
    }

    @Override
    public TaskResponse getTaskById(UUID taskId) {
        return null;
    }

    @Override
    public TaskResponse deleteTaskById(UUID taskId) {
        return null;
    }

    @Override
    public TaskResponse updateTaskById(UUID taskId) {
        return null;
    }

    @Override
    public TaskResponse getAllTasks(int pageNo, int pageSize, String sortBy, String sortDirection) {
        return null;
    }

    @Override
    public TaskResponse assignNewTask(TaskRequest taskRequest) {
        taskRequest.toTask(LocalDate.now(),LocalDate.now());
//        Task task= taskRepository.save(taskRequest.toTask(LocalDate.now(),LocalDate.now()));

//        return task.toResponse(
//                getUserById(task.getCreatedBy().toString(),null),
//                getUserById(task.getAssignedTo().toString(),null),
//                getGroupById(task.getGroupId().toString())
//
//        );
        System.err.println("Hi: "+realm);
            getGroupById(taskRequest.getGroupId());
//        System.out.println(getUserById("a",LocalDate.now()));
        return null;
    }



    public UserResponse getUserById(String id, LocalDate lastModified) {
        System.out.println("Hello: "+realm);
        UserRepresentation userRepresentation = keycloak.realm(realm)
                .users().get("4c0f730b-6d00-4705-a76b-aabbcd1e2770")
                .toRepresentation();
        System.err.println("Hello: "+userRepresentation.getFirstName());
        return new UserResponse(
                UUID.fromString(userRepresentation.getId()),
                userRepresentation.getUsername(),
                userRepresentation.getEmail(),
                userRepresentation.getFirstName(),
                userRepresentation.getLastName(),
                Instant.ofEpochMilli(userRepresentation.getCreatedTimestamp())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate(),
                lastModified
        );
    }

    public GroupResponse getGroupById(UUID groupId) {
        GroupRepresentation groupRepresentation = keycloak.realm(realm)
                .groups()
                .group("f6fd2942-5798-46b2-b214-b33b66b51256")
                .toRepresentation();
        return new GroupResponse(UUID.fromString(groupRepresentation.getId()),groupRepresentation.getName());
    }

}
