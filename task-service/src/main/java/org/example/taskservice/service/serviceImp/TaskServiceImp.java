package org.example.taskservice.service.serviceImp;

import org.example.taskservice.exception.NotFoundException;
import org.example.taskservice.fiegn.FeignUserService;
import org.example.taskservice.model.dto.request.TaskRequest;
import org.example.taskservice.model.dto.response.GroupResponse;
import org.example.taskservice.model.dto.response.TaskResponse;
import org.example.taskservice.model.dto.response.UserGroupResponse;
import org.example.taskservice.model.entity.Task;
import org.example.taskservice.repository.TaskRepository;
import org.example.taskservice.service.TaskService;
import org.example.taskservice.util.SortDirection;
import org.example.taskservice.util.TaskFields;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TaskServiceImp implements TaskService {

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Value("${keycloak-admin.username}")
    private String keycloakAdminUsername;

    @Value("${keycloak-admin.password}")
    private String keycloakAdminPassword;

    private final FeignUserService userService;
    private final TaskRepository taskRepository;

    public TaskServiceImp(Keycloak keycloakAdminClient, FeignUserService userService, TaskRepository taskRepository) {
        this.userService = userService;
        this.taskRepository = taskRepository;
    }

    @Override
    public TaskResponse getTaskById(UUID taskId) {
        if(!taskRepository.findById(taskId).isPresent()){
            throw new NotFoundException("Task with id " + taskId + " not found");
        }
        Task task = taskRepository.findById(taskId).get();

        return task.toResponse(
                userService.getUserById(task.getCreatedBy().toString()).getBody().getPayload(),
                userService.getUserById(task.getAssignedTo().toString()).getBody().getPayload(),
                groupResponseById(task.getGroupId())
        );
    }

    @Override
    public TaskResponse deleteTaskById(UUID taskId) {
        if(!taskRepository.findById(taskId).isPresent()){
            throw new NotFoundException("Task with id " + taskId + " not found");
        }
        taskRepository.deleteById(taskId);
        return null;
    }

    @Override
    public TaskResponse updateTaskById(UUID taskId, TaskRequest taskRequest) {
        if(!taskRepository.findById(taskId).isPresent()){
            throw new NotFoundException("Task with id " + taskId + " not found");
        }
        taskRepository.findById(taskId).ifPresent(task -> {
            task.setGroupId(taskRequest.getGroupId());
            task.setTaskName(taskRequest.getTaskName());
            task.setDescription(taskRequest.getDescription());
            task.setCreatedBy(taskRequest.getCreatedBy());
            task.setAssignedTo(taskRequest.getAssignedTo());
            task.setGroupId(taskRequest.getGroupId());
            task.setLastUpdatedAt(LocalDate.now());
        });
        return getTaskById(taskId) ;
    }

    @Override
    public List<TaskResponse> getAllTasks(int pageNo, int pageSize, TaskFields sortBy, SortDirection sortDirection) {
         List<Task> tasks = taskRepository.findAll(PageRequest.of(pageNo,pageSize,
                Sort.Direction.valueOf(sortDirection.toString()),
                String.valueOf(sortBy))).stream().toList();
         List<TaskResponse> taskResponses = new ArrayList<>();
         for (Task task : tasks) {
             taskResponses.add(new TaskResponse(
                     task.getId(),
                     task.getTaskName(),
                     task.getDescription(),
                     userService.getUserById(task.getCreatedBy().toString()).getBody().getPayload(),
                     userService.getUserById(task.getCreatedBy().toString()).getBody().getPayload(),
                     groupResponseById(task.getGroupId()),
                     task.getCreatedAt(),
                     task.getLastUpdatedAt()));
         }
        return taskResponses;
    }

    @Override
    public TaskResponse assignNewTask(TaskRequest taskRequest) {
        taskRequest.toTask(LocalDate.now(),LocalDate.now());
        Task task= taskRepository.save(taskRequest.toTask(LocalDate.now(),LocalDate.now()));
        task.setCreatedAt(LocalDate.now());
        task.setLastUpdatedAt(LocalDate.now());
        return task.toResponse(
                userService.getUserById(task.getCreatedBy().toString()).getBody().getPayload(),
                userService.getUserById(task.getAssignedTo().toString()).getBody().getPayload(),
                groupResponseById(taskRequest.getGroupId())
        );
    }
    public GroupResponse groupResponseById(UUID taskId) {
       UserGroupResponse userGroupResponse = userService.getAllUsersByGroups(taskId).getBody().getPayload();
       return new GroupResponse(userGroupResponse.getGroupId(),userGroupResponse.getGroupName());
    }
}
