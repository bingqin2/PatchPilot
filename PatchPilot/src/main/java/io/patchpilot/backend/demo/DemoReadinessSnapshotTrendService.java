package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoReadinessSnapshotArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessSnapshotTrendStatus;
import io.patchpilot.backend.demo.domain.DemoReadinessSnapshotTrendVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.DemoReadinessSnapshotArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DemoReadinessSnapshotTrendService {

    private static final String SIDE_EFFECT_CONTRACT = "Demo readiness snapshot trend is read-only over PatchPilot-local snapshot evidence; "
            + "it does not create tasks, call the model, clone repositories, run verification commands, mutate Git, or write to GitHub.";

    private final DemoReadinessSnapshotArchiveRepository archiveRepository;

    public DemoReadinessSnapshotTrendVo getTrendSummary() {
        List<DemoReadinessSnapshotArchiveVo> archives = archiveRepository.listRecentArchives(2);
        if (archives.size() < 2) {
            return noBaselineSummary(archives);
        }
        DemoReadinessSnapshotArchiveVo latest = archives.get(0);
        DemoReadinessSnapshotArchiveVo previous = archives.get(1);
        int readyDelta = latest.readyCheckCount() - previous.readyCheckCount();
        int needsAttentionDelta = latest.needsAttentionCheckCount() - previous.needsAttentionCheckCount();
        int blockedDelta = latest.blockedCheckCount() - previous.blockedCheckCount();
        DemoReadinessSnapshotTrendStatus status = trendStatus(latest, previous, needsAttentionDelta, blockedDelta);
        String summary = trendSummary(status, latest.status(), previous.status());
        String nextAction = nextAction(status);
        return new DemoReadinessSnapshotTrendVo(
                status,
                summary,
                latest.id(),
                previous.id(),
                latest.status(),
                previous.status(),
                readyDelta,
                needsAttentionDelta,
                blockedDelta,
                nextAction,
                markdownReport(status, summary, latest, previous, readyDelta, needsAttentionDelta, blockedDelta, nextAction)
        );
    }

    private static DemoReadinessSnapshotTrendVo noBaselineSummary(List<DemoReadinessSnapshotArchiveVo> archives) {
        DemoReadinessSnapshotArchiveVo latest = archives.isEmpty() ? null : archives.get(0);
        String latestId = latest == null ? null : latest.id();
        DemoReadinessStatus latestStatus = latest == null ? null : latest.status();
        String summary = "At least two readiness snapshots are required to compare demo readiness trend.";
        String nextAction = "Archive another demo readiness snapshot after setup changes or before the next live run.";
        return new DemoReadinessSnapshotTrendVo(
                DemoReadinessSnapshotTrendStatus.NO_BASELINE,
                summary,
                latestId,
                null,
                latestStatus,
                null,
                0,
                0,
                0,
                nextAction,
                noBaselineReport(latestId, latestStatus, summary, nextAction)
        );
    }

    private static DemoReadinessSnapshotTrendStatus trendStatus(
            DemoReadinessSnapshotArchiveVo latest,
            DemoReadinessSnapshotArchiveVo previous,
            int needsAttentionDelta,
            int blockedDelta
    ) {
        int latestRisk = riskScore(latest.status());
        int previousRisk = riskScore(previous.status());
        if (latestRisk < previousRisk || blockedDelta < 0 || (needsAttentionDelta < 0 && blockedDelta <= 0)) {
            return DemoReadinessSnapshotTrendStatus.IMPROVING;
        }
        if (latestRisk > previousRisk || blockedDelta > 0 || (needsAttentionDelta > 0 && blockedDelta >= 0)) {
            return DemoReadinessSnapshotTrendStatus.REGRESSING;
        }
        return DemoReadinessSnapshotTrendStatus.STABLE;
    }

    private static int riskScore(DemoReadinessStatus status) {
        return switch (status) {
            case READY -> 0;
            case NEEDS_ATTENTION -> 1;
            case BLOCKED -> 2;
        };
    }

    private static String trendSummary(
            DemoReadinessSnapshotTrendStatus trendStatus,
            DemoReadinessStatus latestStatus,
            DemoReadinessStatus previousStatus
    ) {
        return switch (trendStatus) {
            case IMPROVING -> "Demo readiness improved from " + previousStatus + " to " + latestStatus + ".";
            case REGRESSING -> "Demo readiness regressed from " + previousStatus + " to " + latestStatus + ".";
            case STABLE -> "Demo readiness stayed at " + latestStatus + ".";
            case NO_BASELINE -> "At least two readiness snapshots are required to compare demo readiness trend.";
        };
    }

    private static String nextAction(DemoReadinessSnapshotTrendStatus status) {
        return switch (status) {
            case IMPROVING -> "Use the latest readiness snapshot as demo evidence or archive one more snapshot immediately before the live run.";
            case REGRESSING -> "Investigate the new readiness warnings or blockers before posting a live /agent fix comment.";
            case STABLE -> "Keep the latest readiness snapshot and resolve remaining warnings before the live run.";
            case NO_BASELINE -> "Archive another demo readiness snapshot after setup changes or before the next live run.";
        };
    }

    private static String markdownReport(
            DemoReadinessSnapshotTrendStatus status,
            String summary,
            DemoReadinessSnapshotArchiveVo latest,
            DemoReadinessSnapshotArchiveVo previous,
            int readyDelta,
            int needsAttentionDelta,
            int blockedDelta,
            String nextAction
    ) {
        return "# PatchPilot Demo Readiness Snapshot Trend\n\n"
                + "- Status: `" + status + "`\n"
                + "- Summary: " + summary + "\n"
                + "- Latest snapshot: `" + latest.id() + "`\n"
                + "- Latest readiness: `" + latest.status() + "`\n"
                + "- Previous snapshot: `" + previous.id() + "`\n"
                + "- Previous readiness: `" + previous.status() + "`\n"
                + "- Ready check delta: `" + signed(readyDelta) + "`\n"
                + "- Needs attention check delta: `" + signed(needsAttentionDelta) + "`\n"
                + "- Blocked check delta: `" + signed(blockedDelta) + "`\n"
                + "- Next action: " + nextAction + "\n"
                + "- Side-effect contract: " + SIDE_EFFECT_CONTRACT + "\n";
    }

    private static String noBaselineReport(
            String latestId,
            DemoReadinessStatus latestStatus,
            String summary,
            String nextAction
    ) {
        return "# PatchPilot Demo Readiness Snapshot Trend\n\n"
                + "- Status: `NO_BASELINE`\n"
                + "- Summary: " + summary + "\n"
                + "- Latest snapshot: `" + valueOrNone(latestId) + "`\n"
                + "- Latest readiness: `" + valueOrNone(latestStatus) + "`\n"
                + "- Previous snapshot: `none`\n"
                + "- Previous readiness: `none`\n"
                + "- Ready check delta: `0`\n"
                + "- Needs attention check delta: `0`\n"
                + "- Blocked check delta: `0`\n"
                + "- Next action: " + nextAction + "\n"
                + "- Side-effect contract: " + SIDE_EFFECT_CONTRACT + "\n";
    }

    private static String signed(int value) {
        return value > 0 ? "+" + value : String.valueOf(value);
    }

    private static String valueOrNone(Object value) {
        return value == null ? "none" : value.toString();
    }
}
