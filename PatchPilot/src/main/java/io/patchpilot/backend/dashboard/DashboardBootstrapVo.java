package io.patchpilot.backend.dashboard;

public record DashboardBootstrapVo(
        boolean adminTokenConfigured,
        boolean adminTokenBootstrapEnabled,
        String adminToken,
        String message,
        String operatorAction
) {
}
