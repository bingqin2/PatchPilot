package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageAcceptanceCloseoutArchiveVo;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageFinalizationCheckVo;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageFinalizationVo;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskEvidencePackageAcceptanceCloseoutArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FixTaskEvidencePackageAcceptanceCloseoutArchiveServiceTests {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-06-28T07:00:00Z"), ZoneOffset.UTC);

    @Test
    void should_archive_ready_finalization_as_local_acceptance_closeout() {
        InMemoryFixTaskEvidencePackageAcceptanceCloseoutArchiveRepository repository =
                new InMemoryFixTaskEvidencePackageAcceptanceCloseoutArchiveRepository();
        FixTaskEvidencePackageAcceptanceCloseoutArchiveService service =
                new FixTaskEvidencePackageAcceptanceCloseoutArchiveService(
                        () -> finalization(true),
                        repository,
                        CLOCK,
                        () -> "task-evidence-closeout-archive-1"
                );

        FixTaskEvidencePackageAcceptanceCloseoutArchiveVo archive = service.archiveCurrentCloseout();

        assertThat(archive.id()).isEqualTo("task-evidence-closeout-archive-1");
        assertThat(archive.status()).isEqualTo("READY");
        assertThat(archive.accepted()).isTrue();
        assertThat(archive.latestArchiveId()).isEqualTo("task-evidence-archive-1");
        assertThat(archive.latestTaskId()).isEqualTo("task-1");
        assertThat(archive.latestDeliveryReceiptId()).isEqualTo("task-evidence-delivery-receipt-1");
        assertThat(archive.deliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(archive.createdAt()).isEqualTo(Instant.parse("2026-06-28T07:00:00Z"));
        assertThat(archive.report())
                .contains("# PatchPilot Task Evidence Acceptance Closeout Archive")
                .contains("# PatchPilot Task Evidence Finalization Gate")
                .contains("does not create tasks, call the model, run tests, mutate Git, send messages, record delivery receipts, or write to GitHub");
        assertThat(service.listRecentArchives()).containsExactly(archive);
        assertThat(service.findArchive("task-evidence-closeout-archive-1")).contains(archive);
    }

    @Test
    void should_reject_archive_when_finalization_is_not_ready() {
        InMemoryFixTaskEvidencePackageAcceptanceCloseoutArchiveRepository repository =
                new InMemoryFixTaskEvidencePackageAcceptanceCloseoutArchiveRepository();
        FixTaskEvidencePackageAcceptanceCloseoutArchiveService service =
                new FixTaskEvidencePackageAcceptanceCloseoutArchiveService(
                        () -> finalization(false),
                        repository,
                        CLOCK,
                        () -> "task-evidence-closeout-archive-1"
                );

        assertThatThrownBy(service::archiveCurrentCloseout)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Task evidence finalization must be READY before archiving acceptance closeout");
        assertThat(service.listRecentArchives()).isEmpty();
    }

    private static FixTaskEvidencePackageFinalizationVo finalization(boolean finalized) {
        return new FixTaskEvidencePackageFinalizationVo(
                finalized ? "READY" : "NEEDS_ATTENTION",
                finalized,
                finalized
                        ? "Task evidence is finalized with a fresh delivery receipt for the current shareable archive."
                        : "Task evidence needs a fresh delivery receipt before final acceptance.",
                finalized
                        ? "Download the finalization report and archive acceptance closeout."
                        : "Record a task evidence delivery receipt first.",
                "task-evidence-archive-1",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/8",
                finalized ? "task-evidence-delivery-receipt-1" : null,
                finalized ? "reviewer@example.com" : null,
                finalized ? "email" : null,
                finalized ? "2026-06-28T06:05:00Z" : null,
                finalized ? "FRESH" : "MISSING",
                finalized,
                finalized ? "Latest delivery receipt matches the current archive." : "No delivery receipt exists.",
                List.of(new FixTaskEvidencePackageFinalizationCheckVo(
                        "Task evidence acceptance",
                        finalized ? "READY" : "NEEDS_ATTENTION",
                        finalized ? "Current task evidence can be accepted." : "Acceptance is blocked.",
                        finalized ? "Archive acceptance closeout." : "Record a delivery receipt."
                )),
                List.of("Shareable task evidence archive is available."),
                "# PatchPilot Task Evidence Finalization Gate\n\n- Status: `" + (finalized ? "READY" : "NEEDS_ATTENTION") + "`\n",
                Instant.parse("2026-06-28T06:30:00Z")
        );
    }
}
