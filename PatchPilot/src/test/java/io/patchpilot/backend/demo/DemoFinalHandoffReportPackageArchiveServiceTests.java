package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalHandoffReportPackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoFinalHandoffReportPackageVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoFinalHandoffReportPackageArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalHandoffReportPackageArchiveServiceTests {

    @Test
    void archives_current_final_handoff_report_package() {
        DemoFinalHandoffReportPackageArchiveService service = new DemoFinalHandoffReportPackageArchiveService(
                DemoFinalHandoffReportPackageArchiveServiceTests::reportPackage,
                new InMemoryDemoFinalHandoffReportPackageArchiveRepository(),
                Clock.fixed(Instant.parse("2026-06-28T11:30:00Z"), ZoneOffset.UTC),
                () -> "final-handoff-package-archive-1"
        );

        DemoFinalHandoffReportPackageArchiveVo archive = service.archiveCurrentReportPackage();

        assertThat(archive.id()).isEqualTo("final-handoff-package-archive-1");
        assertThat(archive.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(archive.downloadReady()).isTrue();
        assertThat(archive.summary()).isEqualTo("Final demo handoff report package is ready to deliver.");
        assertThat(archive.latestArchiveId()).isEqualTo("handoff-archive-1");
        assertThat(archive.latestSessionId()).isEqualTo("demo-session-20260624T003000Z");
        assertThat(archive.latestDeliveryReceiptId()).isEqualTo("delivery-receipt-1");
        assertThat(archive.taskCertificateArchiveId()).isEqualTo("task-evidence-certificate-archive-1");
        assertThat(archive.taskCertificateReady()).isTrue();
        assertThat(archive.readinessChecks()).containsExactly(
                "Archive summary: READY",
                "Share checklist: READY",
                "Share center: READY",
                "Task evidence certificate: READY",
                "Finalization: READY"
        );
        assertThat(archive.requiredAttachments()).contains("Finalization report");
        assertThat(archive.preSendChecks()).contains("Confirm no handoff share checklist warnings remain.");
        assertThat(archive.evidenceNotes()).contains("Latest delivery receipt delivery-receipt-1 is fresh.");
        assertThat(archive.sourceReports()).contains("Handoff finalization");
        assertThat(archive.generatedAt()).isEqualTo(Instant.parse("2026-06-28T11:00:00Z"));
        assertThat(archive.archivedAt()).isEqualTo(Instant.parse("2026-06-28T11:30:00Z"));
        assertThat(archive.report())
                .contains("# PatchPilot Final Demo Handoff Report Package")
                .contains("handoff-archive-1");
        assertThat(service.listRecentArchives()).containsExactly(archive);
        assertThat(service.findArchive("final-handoff-package-archive-1")).contains(archive);
    }

    @Test
    void keeps_only_twenty_recent_report_package_archives() {
        DemoFinalHandoffReportPackageArchiveService service = new DemoFinalHandoffReportPackageArchiveService(
                DemoFinalHandoffReportPackageArchiveServiceTests::reportPackage,
                new InMemoryDemoFinalHandoffReportPackageArchiveRepository(),
                Clock.fixed(Instant.parse("2026-06-28T11:30:00Z"), ZoneOffset.UTC),
                new IncrementingIdSupplier()
        );

        for (int index = 1; index <= 22; index++) {
            service.archiveCurrentReportPackage();
        }

        List<DemoFinalHandoffReportPackageArchiveVo> archives = service.listRecentArchives();
        assertThat(archives).hasSize(20);
        assertThat(archives)
                .extracting(DemoFinalHandoffReportPackageArchiveVo::id)
                .containsExactly(
                        "final-handoff-package-archive-22",
                        "final-handoff-package-archive-21",
                        "final-handoff-package-archive-20",
                        "final-handoff-package-archive-19",
                        "final-handoff-package-archive-18",
                        "final-handoff-package-archive-17",
                        "final-handoff-package-archive-16",
                        "final-handoff-package-archive-15",
                        "final-handoff-package-archive-14",
                        "final-handoff-package-archive-13",
                        "final-handoff-package-archive-12",
                        "final-handoff-package-archive-11",
                        "final-handoff-package-archive-10",
                        "final-handoff-package-archive-9",
                        "final-handoff-package-archive-8",
                        "final-handoff-package-archive-7",
                        "final-handoff-package-archive-6",
                        "final-handoff-package-archive-5",
                        "final-handoff-package-archive-4",
                        "final-handoff-package-archive-3"
                );
        assertThat(service.findArchive("final-handoff-package-archive-1")).isEmpty();
    }

    private static DemoFinalHandoffReportPackageVo reportPackage() {
        return new DemoFinalHandoffReportPackageVo(
                DemoReadinessStatus.READY,
                true,
                "Final demo handoff report package is ready to deliver.",
                "Download this final handoff report package and attach the listed evidence files.",
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                "delivery-receipt-1",
                "task-evidence-certificate-archive-1",
                true,
                List.of(
                        "Archive summary: READY",
                        "Share checklist: READY",
                        "Share center: READY",
                        "Task evidence certificate: READY",
                        "Finalization: READY"
                ),
                List.of(
                        "Handoff package archive handoff-archive-1",
                        "Task evidence acceptance certificate archive task-evidence-certificate-archive-1",
                        "Finalization report"
                ),
                List.of("Confirm no handoff share checklist warnings remain."),
                List.of("Latest delivery receipt delivery-receipt-1 is fresh."),
                List.of("Handoff package archive summary", "Handoff share center", "Handoff finalization"),
                "# PatchPilot Final Demo Handoff Report Package\n\n- Latest archive: `handoff-archive-1`\n",
                Instant.parse("2026-06-28T11:00:00Z")
        );
    }

    private static final class IncrementingIdSupplier implements java.util.function.Supplier<String> {

        private int nextId = 1;

        @Override
        public String get() {
            return "final-handoff-package-archive-" + nextId++;
        }
    }
}
