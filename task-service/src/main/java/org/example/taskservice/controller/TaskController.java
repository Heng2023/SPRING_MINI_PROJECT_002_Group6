package org.example.taskservice.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.example.taskservice.model.dto.request.TaskRequest;
import org.example.taskservice.model.dto.response.ApiResponse;
import org.example.taskservice.service.TaskService;
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
                .message("Success")
                .payload(taskService.getTaskById(taskId))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @DeleteMapping("{taskId}")
    public ResponseEntity<ApiResponse<?>> helloWorld(@PathVariable UUID taskId){
        ApiResponse<?> response = ApiResponse.builder()
                .message("Success")
                .payload(taskService.deleteTaskById(taskId))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PutMapping("{taskId}")
    public ResponseEntity<ApiResponse<?>> updateTaskById(@PathVariable UUID taskId, TaskRequest taskRequest){
        ApiResponse<?> response = ApiResponse.builder()
                .message("Success")
                .payload(taskService.updateTaskById(taskId))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("")
    public ResponseEntity<ApiResponse<?>> getAllTasks(int pageNo, int pageSize, String sortBy, String sortDirection){
        ApiResponse<?> response = ApiResponse.builder()
                .message("Success")
                .payload(taskService.getAllTasks(pageNo,pageSize,sortBy,sortDirection))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PostMapping("")
    public ResponseEntity<ApiResponse<?>> createTask(@RequestBody TaskRequest taskRequest){
        ApiResponse<?> response = ApiResponse.builder()
                .message("Success")
                .payload(taskService.assignNewTask(taskRequest))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
