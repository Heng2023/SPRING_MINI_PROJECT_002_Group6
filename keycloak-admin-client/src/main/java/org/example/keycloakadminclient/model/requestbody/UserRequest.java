package org.example.keycloakadminclient.model.requestbody;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRequest {
    @NotBlank(message = "Username cannot be blank")
    @Size(max = 30, message = "Username can have only 30 characters")
    @Pattern(regexp = "^[^\\s].*$", message = "Username cannot have leading spaces")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 15, message = "Password must be between 8 and 15 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", message = "Password must contain at least one lowercase letter, one uppercase letter, one number, and one special character")
    @Pattern(regexp = "^[^\\s].*$", message = "Password cannot have leading spaces")
    private String password;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Size(min = 11, max = 30, message = "Email must be between 11 and 30 characters long")
    @Pattern(regexp = "^[^\\s].*$", message = "Email cannot have leading spaces")
    private String email;

    @NotBlank(message = "Firstname cannot be blank")
    @Size(max = 30, message = "Firstname can have only 30 characters")
    @Pattern(regexp = "^[^\\s].*$", message = "Firstname cannot have leading spaces")
    private String firstName;

    @NotBlank(message = "Lastname cannot be blank")
    @Size(max = 30, message = "Lastname can have only 30 characters")
    @Pattern(regexp = "^[^\\s].*$", message = "Lastname cannot have leading spaces")
    private String lastName;
}
