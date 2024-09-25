package org.example.taskservice.fiegn;

import org.example.taskservice.config.FeignConfig;
import org.example.taskservice.model.dto.response.ApiResponse;
import org.example.taskservice.model.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "keycloak-admin-client", url = "http://localhost:8082/",
        configuration = FeignConfig.class)
public interface FeignUserService {
    @GetMapping("/api/v1/user/{id}")
    ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable("id") String id);
}
