package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoHandoffShareChecklistVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoHandoffPackageArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class DemoHandoffShareChecklistServiceTests {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-06-24T05:00:00Z"), ZoneOffset.UTC);

    @Test
    void should_mark_handoff_share_checklist_ready_when_latest_archive_is_share_ready() {
        InMemoryDemoHandoffPackageArchiveRepository repository = new InMemoryDemoHandoffPackageArchiveRepository();
        DemoHandoffPackageArchiveService archiveService = archiveService(repository);
        archiveService.archiveCurrentHandoffPackage(DemoSessionReportServiceTests.readyHandoffRequest());
        DemoHandoffShareChecklistService service = new DemoHandoffShareChecklistService(
                new DemoHandoffPackageArchiveSummaryService(repository),
                CLOCK
        );

        DemoHandoffShareChecklistVo checklist = service.getShareChecklist();

        assertThat(checklist.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(checklist.summary()).isEqualTo("Latest handoff archive is ready to share.");
        assertThat(checklist.nextAction()).isEqualTo("Share the latest handoff package summary and archived package with the reviewer.");
        assertThat(checklist.generatedAt()).isEqualTo(Instant.parse("2026-06-24T05:00:00Z"));
        assertThat(checklist.checks())
                .extracting("name", "status")
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("Handoff package archive", DemoReadinessStatus.READY),
                        org.assertj.core.groups.Tuple.tuple("Latest handoff readiness", DemoReadinessStatus.READY),
                        org.assertj.core.groups.Tuple.tuple("Share-ready summary", DemoReadinessStatus.READY),
                        org.assertj.core.groups.Tuple.tuple("Portable evidence", DemoReadinessStatus.READY)
                );
        assertThat(checklist.markdownReport())
                .contains("# PatchPilot Demo Handoff Share Checklist")
                .contains("- Status: `READY`")
                .contains("- Handoff package archive: `READY`")
                .contains("GET /api/demo/handoff-share-checklist is read-only");
    }

    @Test
    void should_request_archive_before_handoff_share_checklist_can_be_shared() {
        DemoHandoffShareChecklistService service = new DemoHandoffShareChecklistService(
                new DemoHandoffPackageArchiveSummaryService(new InMemoryDemoHandoffPackageArchiveRepository()),
                CLOCK
        );

        DemoHandoffShareChecklistVo checklist = service.getShareChecklist();

        assertThat(checklist.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(checklist.summary()).isEqualTo("No handoff package archive is available for sharing.");
        assertThat(checklist.nextAction())
                .isEqualTo("Archive a demo handoff package after a completed live run before sharing handoff evidence.");
        assertThat(checklist.checks())
                .extracting("name", "status")
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("Handoff package archive", DemoReadinessStatus.NEEDS_ATTENTION),
                        org.assertj.core.groups.Tuple.tuple("Latest handoff readiness", DemoReadinessStatus.NEEDS_ATTENTION),
                        org.assertj.core.groups.Tuple.tuple("Share-ready summary", DemoReadinessStatus.NEEDS_ATTENTION),
                        org.assertj.core.groups.Tuple.tuple("Portable evidence", DemoReadinessStatus.NEEDS_ATTENTION)
                );
        assertThat(checklist.markdownReport())
                .contains("- Status: `NEEDS_ATTENTION`")
                .contains("- Handoff package archive: `NEEDS_ATTENTION`");
    }

    private static DemoHandoffPackageArchiveService archiveService(InMemoryDemoHandoffPackageArchiveRepository repository) {
        return new DemoHandoffPackageArchiveService(
                new DemoSessionReportService(DemoSessionReportServiceTests::snapshot),
                new DemoHandoffPackageArchiveSummaryService(repository),
                repository,
                DemoSessionReportServiceTests::snapshot,
                Clock.fixed(Instant.parse("2026-06-24T04:00:00Z"), ZoneOffset.UTC),
                () -> "handoff-archive-1"
        );
    }
}
