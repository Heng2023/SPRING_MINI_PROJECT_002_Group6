package org.example.taskservice.service.serviceImp;

import lombok.AllArgsConstructor;

import org.example.taskservice.fiegn.FeignUserService;
import org.example.taskservice.model.dto.request.TaskRequest;
import org.example.taskservice.model.dto.response.GroupResponse;
import org.example.taskservice.model.dto.response.TaskResponse;
import org.example.taskservice.model.dto.response.UserResponse;
import org.example.taskservice.model.entity.Task;
import org.example.taskservice.repository.TaskRepository;
import org.example.taskservice.service.TaskService;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TaskServiceImp implements TaskService {

    private final FeignUserService userService;
    private final TaskRepository taskRepository;

    @Override
    public TaskResponse getTaskById(UUID taskId) {
        Task task = taskRepository.findById(taskId).get();

        return task.toResponse(
                userService.getUserById(task.getCreatedBy().toString()).getBody().getPayload(),
                userService.getUserById(task.getAssignedTo().toString()).getBody().getPayload(),
                new GroupResponse(UUID.randomUUID(),"Hello")
        );
    }

    @Override
    public TaskResponse deleteTaskById(UUID taskId) {
        taskRepository.deleteById(taskId);
        return null;
    }

    @Override
    public TaskResponse updateTaskById(UUID taskId, TaskRequest taskRequest) {
        taskRepository.findById(taskId).ifPresent(task -> {
            task.setTaskName(taskRequest.getTaskName());
            task.setDescription(taskRequest.getDescription());
            task.setCreatedBy(taskRequest.getCreatedBy());
            task.setAssignedTo(taskRequest.getAssignedTo());
            task.setLastUpdatedAt(LocalDate.now());
        });
        return getTaskById(taskId) ;
    }

    @Override
    public List<TaskResponse> getAllTasks(int pageNo, int pageSize, String sortBy, String sortDirection) {
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
                     new GroupResponse(UUID.randomUUID(),"Hello"),
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
                new GroupResponse(UUID.randomUUID(),"Hello")
        );
    }
}
