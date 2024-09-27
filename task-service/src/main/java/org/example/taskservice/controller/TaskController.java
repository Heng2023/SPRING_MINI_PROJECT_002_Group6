package org.example.taskservice.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.example.taskservice.model.dto.request.TaskRequest;
import org.example.taskservice.model.dto.response.ApiResponse;
import org.example.taskservice.service.TaskService;
import org.example.taskservice.util.SortDirection;
import org.example.taskservice.util.TaskFields;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/task")
@SecurityRequirement(name = "spring-app")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("{taskId}")
    public ResponseEntity<ApiResponse<?>> getTaskById(@PathVariable UUID taskId) {
        ApiResponse<?> response = ApiResponse.builder()
                .message("Task get successfully")
                .payload(taskService.getTaskById(taskId))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("{taskId}")
    public ResponseEntity<ApiResponse<?>> deleteTaskById(@PathVariable UUID taskId){
        ApiResponse<?> response = ApiResponse.builder()
                .message("Delete task successfully")
                .payload(taskService.deleteTaskById(taskId))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("{taskId}")
    public ResponseEntity<ApiResponse<?>> updateTaskById(@PathVariable UUID taskId, @Valid TaskRequest taskRequest){
        ApiResponse<?> response = ApiResponse.builder()
                .message("Task update successfully")
                .payload(taskService.updateTaskById(taskId, taskRequest))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<?>> getAllTasks(
            @RequestParam(defaultValue = "0")  int pageNo,
            @RequestParam(defaultValue = "10")  int pageSize, TaskFields sortBy, SortDirection sortDirection){
        ApiResponse<?> response = ApiResponse.builder()
                .message("All tasks are found")
                .payload(taskService.getAllTasks(pageNo,pageSize,sortBy,sortDirection))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<?>> createTask(@Valid @RequestBody TaskRequest taskRequest){
        ApiResponse<?> response = ApiResponse.builder()
                .message("Task was created successfully")
                .payload(taskService.assignNewTask(taskRequest))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
