package io.patchpilot.backend.github.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "patchpilot.github")
public class GitHubProperties {

    private String token = "";
    private String webhookPublicBaseUrl = "";

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getWebhookPublicBaseUrl() {
        return webhookPublicBaseUrl;
    }

    public void setWebhookPublicBaseUrl(String webhookPublicBaseUrl) {
        this.webhookPublicBaseUrl = webhookPublicBaseUrl;
    }
}
