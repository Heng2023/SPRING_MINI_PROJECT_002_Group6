package org.example.taskservice.service;

import org.example.taskservice.model.dto.request.TaskRequest;
import org.example.taskservice.model.dto.response.TaskResponse;
import org.example.taskservice.util.SortDirection;
import org.example.taskservice.util.TaskFields;

import java.util.List;
import java.util.UUID;

public interface TaskService {

    TaskResponse getTaskById(UUID taskId);

    TaskResponse deleteTaskById(UUID taskId);

    TaskResponse updateTaskById(UUID taskId , TaskRequest taskRequest);

    List<TaskResponse> getAllTasks(int pageNo, int pageSize, TaskFields sortBy, SortDirection sortDirection);

    TaskResponse assignNewTask(TaskRequest taskRequest);
}
