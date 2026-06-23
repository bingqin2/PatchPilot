package io.patchpilot.backend.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@Data
@ConfigurationProperties(prefix = "patchpilot.security")
public class AdminApiSecurityProperties {

    private String adminToken = "";

    public boolean isAdminTokenConfigured() {
        return StringUtils.hasText(adminToken);
    }
}
