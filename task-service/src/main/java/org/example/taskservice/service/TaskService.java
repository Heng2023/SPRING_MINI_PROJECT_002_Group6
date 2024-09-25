package org.example.taskservice.service;

import org.example.taskservice.model.dto.request.TaskRequest;
import org.example.taskservice.model.dto.response.TaskResponse;
import org.example.taskservice.model.dto.response.UserResponse;

import java.util.UUID;

public interface TaskService {
    TaskResponse getTaskById(UUID taskId);

    TaskResponse deleteTaskById(UUID taskId);

    TaskResponse updateTaskById(UUID taskId);

    TaskResponse getAllTasks(int pageNo, int pageSize, String sortBy, String sortDirection);

    TaskResponse assignNewTask(TaskRequest taskRequest);
}
