package io.patchpilot.backend.safety.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "patchpilot.safety")
public class SafetyProperties {

    private List<String> allowedTriggerUsers = new ArrayList<>();
    private List<String> allowedRepositories = new ArrayList<>();
    private boolean modelTriggerClassificationEnabled = false;

    public List<String> getAllowedTriggerUsers() {
        return allowedTriggerUsers;
    }

    public void setAllowedTriggerUsers(List<String> allowedTriggerUsers) {
        this.allowedTriggerUsers = allowedTriggerUsers == null ? new ArrayList<>() : allowedTriggerUsers;
    }

    public List<String> getAllowedRepositories() {
        return allowedRepositories;
    }

    public void setAllowedRepositories(List<String> allowedRepositories) {
        this.allowedRepositories = allowedRepositories == null ? new ArrayList<>() : allowedRepositories;
    }

    public boolean isModelTriggerClassificationEnabled() {
        return modelTriggerClassificationEnabled;
    }

    public void setModelTriggerClassificationEnabled(boolean modelTriggerClassificationEnabled) {
        this.modelTriggerClassificationEnabled = modelTriggerClassificationEnabled;
    }
}
