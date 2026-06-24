package io.patchpilot.backend.demo.domain;

public record DemoEvidenceBundleSummaryVo(
        long adapterFixtureCount,
        long failedAdapterFixtureCount,
        long recentTaskCount,
        long activeQuarantineCount,
        boolean recentPullRequestAvailable
) {
}
