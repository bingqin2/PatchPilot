package io.patchpilot.backend.dashboard;

import io.patchpilot.backend.dashboard.config.DashboardProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class DashboardLinkService {

    private final DashboardProperties dashboardProperties;

    public DashboardLinkService() {
        this(new DashboardProperties());
    }

    @Autowired
    public DashboardLinkService(DashboardProperties dashboardProperties) {
        this.dashboardProperties = dashboardProperties;
    }

    public Optional<String> taskUrl(String taskId) {
        if (!StringUtils.hasText(taskId) || !StringUtils.hasText(dashboardProperties.getBaseUrl())) {
            return Optional.empty();
        }
        return Optional.of(trimTrailingSlash(dashboardProperties.getBaseUrl().trim()) + "/tasks/" + taskId.trim());
    }

    public boolean isBaseUrlConfigured() {
        return StringUtils.hasText(dashboardProperties.getBaseUrl());
    }

    private static String trimTrailingSlash(String value) {
        String trimmed = value;
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }
}
