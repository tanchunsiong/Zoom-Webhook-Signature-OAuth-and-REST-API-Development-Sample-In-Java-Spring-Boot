package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@RestController
public class S2SGetAccessTokenController {

    @Autowired
    private ConfigProperties configProperties;

    @GetMapping("/fetch-bearer-token")
    public String fetchBearerToken() {
        return fetchToken();
    }

    private String fetchToken() {
        try {
            // Create a Basic Authorization header with client credentials
            String credentials = configProperties.getClientId() + ":" + configProperties.getClientSecret();
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

            String apiUrl = "https://zoom.us/oauth/token?grant_type=account_credentials&account_id=" + configProperties.getAccountId();

            // Define the token request parameters
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Basic " + encodedCredentials);
            headers.add("Content-Type", "application/x-www-form-urlencoded");

            // Create the request entity
            HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

            // Send the token request
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, Map.class);

            // Extract the access token from the response
            Map<String, String> responseBody = response.getBody();
            String accessToken = responseBody.get("access_token");

            // Return the access token
            return accessToken;

        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
