package org.example.taskservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    private final KeycloakService keycloakService;

    public FeignConfig(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                // Get the access token from the Keycloak service
                String token = keycloakService.getAccessToken(); // Pass the realm name as needed
                if (token == null) {
                   token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJhQzQ4TEdfR0w4bmZkNGhXMmxucEYteVd4eWtTTXpxUXhnZ2RkZjdMNThNIn0.eyJleHAiOjE3MjcyNTYwMzAsImlhdCI6MTcyNzI1NTczMCwianRpIjoiMjlmOThjNWEtNThmMC00NzZhLWFkZDgtN2Y4N2I1Y2MyYzQ4IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9wcm9qZWN0IiwiYXVkIjoiYWNjb3VudCIsInN1YiI6IjFkYTcwMzJiLWZmMTctNGRmYy1iNTY2LTJkNGYwNzgyYzQyMSIsInR5cCI6IkJlYXJlciIsImF6cCI6ImtleWNsb2FrIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJkZWZhdWx0LXJvbGVzLXByb2plY3QiLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJjbGllbnRIb3N0IjoiMTkyLjE2OC42NS4xIiwicHJlZmVycmVkX3VzZXJuYW1lIjoic2VydmljZS1hY2NvdW50LWtleWNsb2FrIiwiY2xpZW50QWRkcmVzcyI6IjE5Mi4xNjguNjUuMSIsImNsaWVudF9pZCI6ImtleWNsb2FrIn0.hY6p4OtAuX66Ftg3k5YDg6khARwdPK1KlcXWv8WN0z171p8UCjbkV8JlR6IkAiyUZohkvjBBw0bQQH3zTW1_CpaTTnMmRV-O9fGTTAzwnTkd0L-48C3oy-3Cz66fbCkwxsSBMATqlVLq8Igrfv_fBP1IJn1lXJNaWmsiRbFzoufNV06oa0PsWcPJxOAzYkIB8Yy8YvBdV9sPmB51LUz2YAwffNHNyEJ8AWrXyhqB8UcqQ3fUVIpAFQ5FxaVyklxTLZZK2ooojrUd_VhKoSkkFwagiReFQ0X9rO5OergTA03yPUl9l936y5ralQF5HGBYJgJRZhGeaxGhu4dX809M0A";
                         }
                requestTemplate.header("Authorization", "Bearer " + token);
            }
        };
    }
}

