package org.example.keycloakadminclient.configuration;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakClientConfig {
    @Value("${keycloak.credentials.username}")
    private String username;
    @Value("${keycloak.credentials.password}")
    private String password;
    @Value("${keycloak.credentials.secret}")
    private String secretKey;
    @Value("${keycloak.resource}")
    private String clientId;
    @Value("${keycloak.auth-server-url}")
    private String authUrl;
    @Value("${keycloak.realm}")
    private String realm;

    @Bean
    public Keycloak keycloak() {

        // If you want to use client credentials instead of the password grant type,
        // comment out the previous return statement and use the one below:

         return KeycloakBuilder.builder()
                 .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                 .serverUrl(authUrl)
                 .realm(realm)
                 .clientId(clientId)
                 .clientSecret(secretKey)
                 .build();
    }
}