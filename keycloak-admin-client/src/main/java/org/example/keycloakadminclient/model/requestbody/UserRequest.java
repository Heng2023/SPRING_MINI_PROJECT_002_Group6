package org.example.keycloakadminclient.model.requestbody;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRequest {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
}
