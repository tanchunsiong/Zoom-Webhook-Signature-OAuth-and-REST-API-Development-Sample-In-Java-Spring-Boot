package com.example.demo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "meeting")
public class ConfigProperties {
    private String sdkClientKey;
    private String sdkClientSecret;

    public String getSdkClientKey() {
        return sdkClientKey;
    }

    public void setSdkClientKey(String sdkClientKey) {
        this.sdkClientKey = sdkClientKey;
    }

    public String getSdkClientSecret() {
        return sdkClientSecret;
    }

    public void setSdkClientSecret(String sdkClientSecret) {
        this.sdkClientSecret = sdkClientSecret;
    }



    private String webhookSecretToken;

    public String getWebhookSecretToken() {
        return webhookSecretToken;
    }

    public void setWebhookSecretToken(String webhookSecretToken) {
        this.webhookSecretToken = webhookSecretToken;
    }

    

    private String clientId;
    private String clientSecret;
    private String accountId;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}




