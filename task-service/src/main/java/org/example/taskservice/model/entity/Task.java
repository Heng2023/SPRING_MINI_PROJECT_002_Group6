package org.example.taskservice.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.taskservice.model.dto.response.GroupResponse;
import org.example.taskservice.model.dto.response.TaskResponse;
import org.example.taskservice.model.dto.response.UserResponse;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.util.UUID;

@Entity(name = "task")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String taskName;
    private String description;
    private UUID createdBy;
    private UUID assignedTo;
    private UUID groupId;
    @CreatedDate
    private LocalDate createdAt;
    @LastModifiedDate
    private LocalDate lastUpdatedAt;

    public TaskResponse toResponse(UserResponse createdByUser, UserResponse assignedToUser, GroupResponse group) {
       return new TaskResponse(this.id,
               this.taskName,
               this.description,
               createdByUser,
               assignedToUser,
               group,
               this.createdAt,
               this.lastUpdatedAt);
    }
}
