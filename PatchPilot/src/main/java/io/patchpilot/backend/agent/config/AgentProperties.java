package io.patchpilot.backend.agent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "patchpilot.agent")
public class AgentProperties {

    private String provider = "openai-compatible";
    private String model = "gpt-4.1-mini";
    private String baseUrl = "https://api.openai.com/v1";
    private String apiKey = "";
    private Cost cost = new Cost();

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Cost getCost() {
        return cost;
    }

    public void setCost(Cost cost) {
        this.cost = cost;
    }

    public static class Cost {

        private double promptTokenUsd = 0.0;
        private double completionTokenUsd = 0.0;

        public double getPromptTokenUsd() {
            return promptTokenUsd;
        }

        public void setPromptTokenUsd(double promptTokenUsd) {
            this.promptTokenUsd = promptTokenUsd;
        }

        public double getCompletionTokenUsd() {
            return completionTokenUsd;
        }

        public void setCompletionTokenUsd(double completionTokenUsd) {
            this.completionTokenUsd = completionTokenUsd;
        }
    }
}
