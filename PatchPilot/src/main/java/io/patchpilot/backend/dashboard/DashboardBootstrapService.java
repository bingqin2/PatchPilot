package io.patchpilot.backend.dashboard;

import io.patchpilot.backend.security.config.AdminApiSecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardBootstrapService {

    private static final String BOOTSTRAP_FLAG = "PATCHPILOT_DASHBOARD_ADMIN_TOKEN_BOOTSTRAP_ENABLED";

    private final AdminApiSecurityProperties securityProperties;

    public DashboardBootstrapVo getBootstrap() {
        boolean adminTokenConfigured = securityProperties.isAdminTokenConfigured();
        boolean adminTokenBootstrapEnabled = securityProperties.isDashboardAdminTokenBootstrapEnabled();
        String adminToken = adminTokenConfigured && adminTokenBootstrapEnabled ? securityProperties.getAdminToken() : null;
        if (adminToken != null) {
            return new DashboardBootstrapVo(
                    true,
                    true,
                    adminToken,
                    "Local dashboard admin token bootstrap is enabled.",
                    "The dashboard can store this token for the current local browser."
            );
        }
        if (!adminTokenConfigured) {
            return new DashboardBootstrapVo(
                    false,
                    adminTokenBootstrapEnabled,
                    null,
                    "No admin token is configured.",
                    "Set PATCHPILOT_ADMIN_TOKEN to protect operator APIs, then enter it manually in the dashboard header."
            );
        }
        return new DashboardBootstrapVo(
                true,
                false,
                null,
                "Local dashboard admin token bootstrap is disabled.",
                "Set " + BOOTSTRAP_FLAG + "=true only for trusted local development, or enter PATCHPILOT_ADMIN_TOKEN manually in the dashboard header."
        );
    }
}
