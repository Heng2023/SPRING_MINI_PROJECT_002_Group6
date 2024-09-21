package org.example.taskservice.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/task")
@SecurityRequirement(name = "spring-app")
public class TaskController {
    @GetMapping
    public String helloWorld(){
        return "Hello World";
    }
}
