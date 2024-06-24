package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class JwtController {

    @Autowired
    private ConfigProperties configProperties;

    @GetMapping("/meeting")
    public Map<String, String> generateJwt(@RequestParam String meetingNumber, @RequestParam int role) {
        try {
            String token = generateToken(configProperties.getSdkClientKey(), configProperties.getSdkClientSecret(), meetingNumber, role);
            Map<String, String> response = new HashMap<>();
            response.put("signature", token);
            response.put("sdkKey", configProperties.getSdkClientKey());
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error generating JWT");
            return errorResponse;
        }
    }

    private String generateToken(String sdkKey, String sdkSecret, String meetingNumber, int role) throws Exception {
        long now = System.currentTimeMillis() / 1000;
        long iat = now - 5 * 60; // 5 minutes ago
        long exp = now + 30 * 60; // 30 minutes later

        // Create header
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("typ", "JWT");
        headerMap.put("alg", "HS256");
        String header = new ObjectMapper().writeValueAsString(headerMap);
        String headerEncoded = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(header.getBytes(StandardCharsets.UTF_8));

        // Create payload
        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("sdkKey", sdkKey);
        payloadMap.put("mn", meetingNumber);
        payloadMap.put("role", role);
        payloadMap.put("iat", iat);
        payloadMap.put("exp", exp);
        payloadMap.put("appKey", sdkKey);
        payloadMap.put("tokenExp", exp);
        String payload = new ObjectMapper().writeValueAsString(payloadMap);
        String payloadEncoded = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payload.getBytes(StandardCharsets.UTF_8));

        // Create signature
        byte[] signature = hmacSha256(headerEncoded + "." + payloadEncoded, sdkSecret);
        String signatureEncoded = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(signature);

        return headerEncoded + "." + payloadEncoded + "." + signatureEncoded;
    }

    private byte[] hmacSha256(String data, String secret) throws Exception {
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256Hmac.init(secretKey);
        return sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }
}
