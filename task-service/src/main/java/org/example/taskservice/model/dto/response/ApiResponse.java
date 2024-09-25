package org.example.taskservice.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ApiResponse<T> {
    private String message;
    private T payload;
    private LocalDate createdAt;
    private LocalDate lastUpdatedAt;
}
