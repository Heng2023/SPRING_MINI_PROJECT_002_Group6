package org.example.taskservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse {
    private UUID id;
    private String taskName;
    private String description;
    private UserResponse createdBy;
    private UserResponse assignedTo;
    private GroupResponse group;
    private LocalDate createdAt;
    private LocalDate lastUpdatedAt;
}
