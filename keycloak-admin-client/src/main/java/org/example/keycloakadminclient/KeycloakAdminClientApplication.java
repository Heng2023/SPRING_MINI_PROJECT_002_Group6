package org.example.keycloakadminclient;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SecurityScheme(
        name = "spring-app",
        type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(
                clientCredentials = @OAuthFlow(
                        tokenUrl = "http://localhost:8080/realms/project/protocol/openid-connect/token"
                )
        )
)
@OpenAPIDefinition(
        servers = @Server(url = "/")
)
public class KeycloakAdminClientApplication {

    public static void main(String[] args) { SpringApplication.run(KeycloakAdminClientApplication.class, args); }

}
