package io.patchpilot.backend.safety.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "patchpilot.safety")
public class SafetyProperties {

    private List<String> allowedTriggerUsers = new ArrayList<>();
    private List<String> allowedRepositories = new ArrayList<>();
    private boolean modelTriggerClassificationEnabled = false;
    private boolean triggerRateLimitEnabled = true;
    private long triggerRateLimitWindowMs = 600_000;
    private int triggerRateLimitMaxPerTriggerUser = 30;
    private int triggerRateLimitMaxPerRepository = 60;
    private int triggerRateLimitMaxPerIssue = 20;
    private boolean rejectedTriggerQuarantineEnabled = true;
    private long rejectedTriggerQuarantineWindowMs = 600_000;
    private int rejectedTriggerQuarantineThreshold = 5;
    private long rejectedTriggerQuarantineCooldownMs = 1_800_000;

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

    public boolean isTriggerRateLimitEnabled() {
        return triggerRateLimitEnabled;
    }

    public void setTriggerRateLimitEnabled(boolean triggerRateLimitEnabled) {
        this.triggerRateLimitEnabled = triggerRateLimitEnabled;
    }

    public long getTriggerRateLimitWindowMs() {
        return triggerRateLimitWindowMs;
    }

    public void setTriggerRateLimitWindowMs(long triggerRateLimitWindowMs) {
        this.triggerRateLimitWindowMs = triggerRateLimitWindowMs;
    }

    public int getTriggerRateLimitMaxPerTriggerUser() {
        return triggerRateLimitMaxPerTriggerUser;
    }

    public void setTriggerRateLimitMaxPerTriggerUser(int triggerRateLimitMaxPerTriggerUser) {
        this.triggerRateLimitMaxPerTriggerUser = triggerRateLimitMaxPerTriggerUser;
    }

    public int getTriggerRateLimitMaxPerRepository() {
        return triggerRateLimitMaxPerRepository;
    }

    public void setTriggerRateLimitMaxPerRepository(int triggerRateLimitMaxPerRepository) {
        this.triggerRateLimitMaxPerRepository = triggerRateLimitMaxPerRepository;
    }

    public int getTriggerRateLimitMaxPerIssue() {
        return triggerRateLimitMaxPerIssue;
    }

    public void setTriggerRateLimitMaxPerIssue(int triggerRateLimitMaxPerIssue) {
        this.triggerRateLimitMaxPerIssue = triggerRateLimitMaxPerIssue;
    }

    public boolean isRejectedTriggerQuarantineEnabled() {
        return rejectedTriggerQuarantineEnabled;
    }

    public void setRejectedTriggerQuarantineEnabled(boolean rejectedTriggerQuarantineEnabled) {
        this.rejectedTriggerQuarantineEnabled = rejectedTriggerQuarantineEnabled;
    }

    public long getRejectedTriggerQuarantineWindowMs() {
        return rejectedTriggerQuarantineWindowMs;
    }

    public void setRejectedTriggerQuarantineWindowMs(long rejectedTriggerQuarantineWindowMs) {
        this.rejectedTriggerQuarantineWindowMs = rejectedTriggerQuarantineWindowMs;
    }

    public int getRejectedTriggerQuarantineThreshold() {
        return rejectedTriggerQuarantineThreshold;
    }

    public void setRejectedTriggerQuarantineThreshold(int rejectedTriggerQuarantineThreshold) {
        this.rejectedTriggerQuarantineThreshold = rejectedTriggerQuarantineThreshold;
    }

    public long getRejectedTriggerQuarantineCooldownMs() {
        return rejectedTriggerQuarantineCooldownMs;
    }

    public void setRejectedTriggerQuarantineCooldownMs(long rejectedTriggerQuarantineCooldownMs) {
        this.rejectedTriggerQuarantineCooldownMs = rejectedTriggerQuarantineCooldownMs;
    }
}
