package org.example.taskservice.model.dto.request;

import lombok.Data;
import org.example.taskservice.model.entity.Task;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class TaskRequest {
    private String taskName;
    private String description;
    private UUID createdBy;
    private UUID assignedTo;
    private UUID groupId;

    public Task toTask(LocalDate createdAt, LocalDate lastModifiedAt) {
        return new Task(null,this.taskName,this.description,this.createdBy,this.assignedTo,this.groupId,createdAt,lastModifiedAt );
    }
}
