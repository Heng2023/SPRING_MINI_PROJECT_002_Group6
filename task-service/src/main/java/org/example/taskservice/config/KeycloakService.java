package org.example.taskservice.config;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class KeycloakService {

    private static final String TOKEN_URL = "http://localhost:8080/realms/project/protocol/openid-connect/token";
    private static final String CLIENT_ID = "keycloak";
    private static final String CLIENT_SECRET = "SvES0qGVzCbxeaVnr1cwmcqZohYwdpxG";
    private static final String GRANT_TYPE = "client_credentials";

    public String getAccessToken() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", CLIENT_ID);
        body.add("client_secret", CLIENT_SECRET);
        body.add("grant_type", GRANT_TYPE);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<AccessTokenResponse> response = restTemplate.exchange(TOKEN_URL, HttpMethod.POST, entity, AccessTokenResponse.class);
        return response.getBody().getAccessToken();
    }
}
