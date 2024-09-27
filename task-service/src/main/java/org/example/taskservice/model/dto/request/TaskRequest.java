package org.example.taskservice.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.taskservice.model.entity.Task;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class TaskRequest {
    @NotBlank(message = "Task name cannot be blank")
    private String taskName;
    @NotBlank(message = "Description cannot be blank")
    private String description;
    @NotNull(message = "Created by cannot be null")
    private UUID createdBy;
    @NotNull(message = "Assigned to cannot be null")
    private UUID assignedTo;
    @NotNull(message = "Group ID cannot be null")
    private UUID groupId;

    public Task toTask(LocalDate createdAt, LocalDate lastModifiedAt) {
        return new Task(null,this.taskName,this.description,this.createdBy,this.assignedTo,this.groupId,createdAt,lastModifiedAt );
    }
}
