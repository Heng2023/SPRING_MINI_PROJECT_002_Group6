package org.example.taskservice.fiegn;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.example.taskservice.config.FeignClientInterceptor;
import org.example.taskservice.model.dto.response.ApiResponse;
import org.example.taskservice.model.dto.response.UserGroupResponse;
import org.example.taskservice.model.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.UUID;


@FeignClient(name = "keycloak-admin-client",
        url = "http://localhost:8082/",
        configuration = FeignClientInterceptor.class)
public interface FeignUserService {

    @CircuitBreaker(name = "keycloak-admin-client", fallbackMethod = "fallbackGetUserById")
    @GetMapping("/api/v1/user/{id}")
    ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable("id") String id);
    default ResponseEntity<ApiResponse<UserResponse>> fallbackGetUserById(String id, Throwable throwable) {
        UserResponse userResponse = new UserResponse(UUID.fromString(id),"SunMario",
                "thy.sopheak098@gmail.com",
                "Sopheak",
                "Thy",
                LocalDate.now(),
                LocalDate.now());
        return ResponseEntity.status(503)
                .body(ApiResponse.<UserResponse>builder()
                        .message("Service is temporarily unavailable. Please try again later.")
                        .payload(userResponse).build());
    }

    @GetMapping("/api/v1/group/{groupId}/users")
    @CircuitBreaker(name = "keycloak-admin-client-2", fallbackMethod = "fallback")
    ResponseEntity<ApiResponse<UserGroupResponse>> getAllUsersByGroups(@PathVariable UUID groupId);
    default ResponseEntity<ApiResponse<UserGroupResponse>>fallback(String id, Throwable throwable) {
        UserResponse userResponse = new UserResponse(UUID.fromString(id),"SunMario",
                "thy.sopheak098@gmail.com",
                "Sopheak",
                "Thy",
                LocalDate.now(),
                LocalDate.now());
        return null;
    }
}
