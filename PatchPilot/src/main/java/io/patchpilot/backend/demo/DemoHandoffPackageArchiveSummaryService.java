package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveSummaryVo;
import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.DemoHandoffPackageArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DemoHandoffPackageArchiveSummaryService {

    private static final int MAX_ARCHIVES = 20;

    private final DemoHandoffPackageArchiveRepository archiveRepository;

    public DemoHandoffPackageArchiveSummaryVo getArchiveSummary() {
        List<DemoHandoffPackageArchiveVo> archives = archiveRepository.listRecentArchives(MAX_ARCHIVES);
        if (archives.isEmpty()) {
            String nextAction = "Archive a demo handoff package after a completed live run before sharing handoff evidence.";
            return new DemoHandoffPackageArchiveSummaryVo(
                    "NO_ARCHIVE",
                    false,
                    0,
                    null,
                    null,
                    null,
                    null,
                    "No handoff package archive has been captured.",
                    nextAction,
                    formatSummaryReport("NO_ARCHIVE", false, 0, null, null, null, null, "No handoff package archive has been captured.", nextAction)
            );
        }

        DemoHandoffPackageArchiveVo latest = archives.get(0);
        boolean shareReady = latest.handoffReadinessStatus() == DemoReadinessStatus.READY;
        String status = latest.handoffReadinessStatus().name();
        String summary = shareReady
                ? "Latest archived handoff package is READY and can be shared."
                : "Latest archived handoff package needs attention before it is shared.";
        return new DemoHandoffPackageArchiveSummaryVo(
                status,
                shareReady,
                archives.size(),
                latest.id(),
                latest.sessionId(),
                latest.handoffReadinessStatus(),
                latest.createdAt(),
                summary,
                latest.handoffReadinessNextAction(),
                formatSummaryReport(
                        status,
                        shareReady,
                        archives.size(),
                        latest.id(),
                        latest.sessionId(),
                        latest.handoffReadinessStatus(),
                        latest.createdAt(),
                        summary,
                        latest.handoffReadinessNextAction()
                )
        );
    }

    private static String formatSummaryReport(
            String status,
            boolean shareReady,
            int archiveCount,
            String latestArchiveId,
            String latestSessionId,
            DemoReadinessStatus latestHandoffReadinessStatus,
            Instant latestCreatedAt,
            String summary,
            String nextAction
    ) {
        return """
                # PatchPilot Handoff Package Archive Summary

                - Status: `%s`
                - Share ready: `%s`
                - Archive count: `%d`
                - Latest archive: `%s`
                - Latest session: `%s`
                - Latest handoff readiness: `%s`
                - Latest created at: `%s`

                ## Summary

                %s

                ## Next Action

                %s

                ## Side-Effect Contract

                GET /api/demo/handoff-package-archives/summary is read-only: it does not create tasks, call the model, run tests, mutate Git, or write to GitHub.
                """.formatted(
                status,
                shareReady,
                archiveCount,
                valueOrNone(latestArchiveId),
                valueOrNone(latestSessionId),
                latestHandoffReadinessStatus == null ? "none" : latestHandoffReadinessStatus.name(),
                latestCreatedAt == null ? "none" : latestCreatedAt,
                summary,
                nextAction
        );
    }

    private static String valueOrNone(String value) {
        return value == null || value.isBlank() ? "none" : value;
    }
}
