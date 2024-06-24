package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
public class WebhookController {

    @Autowired
    private ConfigProperties configProperties;

    @PostMapping("/webhook")
    public ResponseEntity<Map<String, String>> handleWebhookPost(@RequestBody Map<String, Object> body) {
        return handleWebhookRequest(body, configProperties.getWebhookSecretToken());
    }

    @GetMapping("/webhook")
    public ResponseEntity<Map<String, String>> handleWebhookGet() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(null);
    }

    private ResponseEntity<Map<String, String>> handleWebhookRequest(Map<String, Object> body, String secretToken) {
        if (body.get("event") != null && body.get("event").equals("endpoint.url_validation")) {
            Map<String, Object> payload = (Map<String, Object>) body.get("payload");
            String plainToken = payload.get("plainToken").toString();

            String hashForValidate = hmacSha256(plainToken, secretToken);

            Map<String, String> response = new HashMap<>();
            response.put("plainToken", plainToken);
            response.put("encryptedToken", hashForValidate);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.ok().build();
        }
    }

    private String hmacSha256(String data, String secret) {
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKey);
            byte[] hash = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate HMAC SHA-256", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
