package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoReadinessSnapshotArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessSnapshotTrendStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessSnapshotTrendVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoReadinessSnapshotArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoReadinessSnapshotTrendServiceTests {

    @Test
    void should_report_no_baseline_when_fewer_than_two_readiness_snapshots_exist() {
        InMemoryDemoReadinessSnapshotArchiveRepository repository = new InMemoryDemoReadinessSnapshotArchiveRepository();
        repository.save(snapshot("snapshot-1", DemoReadinessStatus.NEEDS_ATTENTION, 7, 2, 0, "2026-06-27T04:00:00Z"));
        DemoReadinessSnapshotTrendService service = new DemoReadinessSnapshotTrendService(repository);

        DemoReadinessSnapshotTrendVo summary = service.getTrendSummary();

        assertThat(summary.status()).isEqualTo(DemoReadinessSnapshotTrendStatus.NO_BASELINE);
        assertThat(summary.summary()).isEqualTo("At least two readiness snapshots are required to compare demo readiness trend.");
        assertThat(summary.latestSnapshotId()).isEqualTo("snapshot-1");
        assertThat(summary.previousSnapshotId()).isNull();
        assertThat(summary.readyCheckDelta()).isZero();
        assertThat(summary.needsAttentionCheckDelta()).isZero();
        assertThat(summary.blockedCheckDelta()).isZero();
        assertThat(summary.nextAction()).isEqualTo("Archive another demo readiness snapshot after setup changes or before the next live run.");
        assertThat(summary.markdownReport())
                .contains("# PatchPilot Demo Readiness Snapshot Trend")
                .contains("- Status: `NO_BASELINE`")
                .contains("- Latest snapshot: `snapshot-1`")
                .contains("- Previous snapshot: `none`")
                .contains("Archive another demo readiness snapshot");
    }

    @Test
    void should_report_improving_trend_when_latest_snapshot_has_lower_risk_status_and_counts() {
        InMemoryDemoReadinessSnapshotArchiveRepository repository = new InMemoryDemoReadinessSnapshotArchiveRepository();
        repository.save(snapshot("snapshot-old", DemoReadinessStatus.BLOCKED, 5, 2, 2, "2026-06-27T04:00:00Z"));
        repository.save(snapshot("snapshot-new", DemoReadinessStatus.READY, 9, 0, 0, "2026-06-27T05:00:00Z"));
        DemoReadinessSnapshotTrendService service = new DemoReadinessSnapshotTrendService(repository);

        DemoReadinessSnapshotTrendVo summary = service.getTrendSummary();

        assertThat(summary.status()).isEqualTo(DemoReadinessSnapshotTrendStatus.IMPROVING);
        assertThat(summary.summary()).isEqualTo("Demo readiness improved from BLOCKED to READY.");
        assertThat(summary.latestSnapshotId()).isEqualTo("snapshot-new");
        assertThat(summary.previousSnapshotId()).isEqualTo("snapshot-old");
        assertThat(summary.latestReadinessStatus()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(summary.previousReadinessStatus()).isEqualTo(DemoReadinessStatus.BLOCKED);
        assertThat(summary.readyCheckDelta()).isEqualTo(4);
        assertThat(summary.needsAttentionCheckDelta()).isEqualTo(-2);
        assertThat(summary.blockedCheckDelta()).isEqualTo(-2);
        assertThat(summary.nextAction()).isEqualTo("Use the latest readiness snapshot as demo evidence or archive one more snapshot immediately before the live run.");
        assertThat(summary.markdownReport())
                .contains("- Status: `IMPROVING`")
                .contains("- Latest readiness: `READY`")
                .contains("- Previous readiness: `BLOCKED`")
                .contains("- Ready check delta: `+4`")
                .contains("- Needs attention check delta: `-2`")
                .contains("- Blocked check delta: `-2`");
    }

    @Test
    void should_report_regressing_trend_when_latest_snapshot_has_more_blocked_checks() {
        InMemoryDemoReadinessSnapshotArchiveRepository repository = new InMemoryDemoReadinessSnapshotArchiveRepository();
        repository.save(snapshot("snapshot-old", DemoReadinessStatus.READY, 9, 0, 0, "2026-06-27T04:00:00Z"));
        repository.save(snapshot("snapshot-new", DemoReadinessStatus.NEEDS_ATTENTION, 7, 1, 1, "2026-06-27T05:00:00Z"));
        DemoReadinessSnapshotTrendService service = new DemoReadinessSnapshotTrendService(repository);

        DemoReadinessSnapshotTrendVo summary = service.getTrendSummary();

        assertThat(summary.status()).isEqualTo(DemoReadinessSnapshotTrendStatus.REGRESSING);
        assertThat(summary.summary()).isEqualTo("Demo readiness regressed from READY to NEEDS_ATTENTION.");
        assertThat(summary.readyCheckDelta()).isEqualTo(-2);
        assertThat(summary.needsAttentionCheckDelta()).isEqualTo(1);
        assertThat(summary.blockedCheckDelta()).isEqualTo(1);
        assertThat(summary.nextAction()).isEqualTo("Investigate the new readiness warnings or blockers before posting a live /agent fix comment.");
    }

    @Test
    void should_report_stable_trend_when_latest_and_previous_snapshots_have_same_risk() {
        InMemoryDemoReadinessSnapshotArchiveRepository repository = new InMemoryDemoReadinessSnapshotArchiveRepository();
        repository.save(snapshot("snapshot-old", DemoReadinessStatus.NEEDS_ATTENTION, 7, 2, 0, "2026-06-27T04:00:00Z"));
        repository.save(snapshot("snapshot-new", DemoReadinessStatus.NEEDS_ATTENTION, 7, 2, 0, "2026-06-27T05:00:00Z"));
        DemoReadinessSnapshotTrendService service = new DemoReadinessSnapshotTrendService(repository);

        DemoReadinessSnapshotTrendVo summary = service.getTrendSummary();

        assertThat(summary.status()).isEqualTo(DemoReadinessSnapshotTrendStatus.STABLE);
        assertThat(summary.summary()).isEqualTo("Demo readiness stayed at NEEDS_ATTENTION.");
        assertThat(summary.nextAction()).isEqualTo("Keep the latest readiness snapshot and resolve remaining warnings before the live run.");
    }

    private static DemoReadinessSnapshotArchiveVo snapshot(
            String id,
            DemoReadinessStatus status,
            int ready,
            int needsAttention,
            int blocked,
            String createdAt
    ) {
        return new DemoReadinessSnapshotArchiveVo(
                id,
                status,
                "Snapshot " + id + " is " + status + ".",
                ready,
                needsAttention,
                blocked,
                Instant.parse(createdAt),
                "# Snapshot " + id
        );
    }
}
