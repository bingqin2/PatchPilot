package io.patchpilot.backend.dashboard.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "patchpilot.dashboard")
public class DashboardProperties {

    private String baseUrl = "";
}
