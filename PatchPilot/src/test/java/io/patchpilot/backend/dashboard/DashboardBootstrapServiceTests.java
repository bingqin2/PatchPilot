package io.patchpilot.backend.dashboard;

import io.patchpilot.backend.security.config.AdminApiSecurityProperties;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DashboardBootstrapServiceTests {

    @Test
    void should_not_expose_admin_token_when_bootstrap_is_disabled() {
        AdminApiSecurityProperties properties = new AdminApiSecurityProperties();
        properties.setAdminToken("test-admin-token");
        properties.setDashboardAdminTokenBootstrapEnabled(false);

        DashboardBootstrapVo bootstrap = new DashboardBootstrapService(properties).getBootstrap();

        assertThat(bootstrap.adminTokenConfigured()).isTrue();
        assertThat(bootstrap.adminTokenBootstrapEnabled()).isFalse();
        assertThat(bootstrap.adminToken()).isNull();
        assertThat(bootstrap.operatorAction()).contains("PATCHPILOT_DASHBOARD_ADMIN_TOKEN_BOOTSTRAP_ENABLED");
    }

    @Test
    void should_expose_admin_token_only_when_bootstrap_is_enabled() {
        AdminApiSecurityProperties properties = new AdminApiSecurityProperties();
        properties.setAdminToken("test-admin-token");
        properties.setDashboardAdminTokenBootstrapEnabled(true);

        DashboardBootstrapVo bootstrap = new DashboardBootstrapService(properties).getBootstrap();

        assertThat(bootstrap.adminTokenConfigured()).isTrue();
        assertThat(bootstrap.adminTokenBootstrapEnabled()).isTrue();
        assertThat(bootstrap.adminToken()).isEqualTo("test-admin-token");
        assertThat(bootstrap.operatorAction()).contains("local browser");
    }
}
