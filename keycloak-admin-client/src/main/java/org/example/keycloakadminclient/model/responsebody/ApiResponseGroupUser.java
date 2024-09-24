package org.example.keycloakadminclient.model.responsebody;

import lombok.*;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ApiResponseGroupUser<T> {
    private String message;
    private Integer code;
    private T payload;
    private HttpStatus status;
    private LocalDateTime timestamp;
}
