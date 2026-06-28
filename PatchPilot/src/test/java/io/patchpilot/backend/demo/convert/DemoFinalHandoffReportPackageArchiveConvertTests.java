package io.patchpilot.backend.demo.convert;

import io.patchpilot.backend.demo.domain.DemoFinalHandoffReportPackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalHandoffReportPackageArchiveEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalHandoffReportPackageArchiveConvertTests {

    @Test
    void converts_final_handoff_report_package_archive_to_entity_and_back() {
        DemoFinalHandoffReportPackageArchiveVo archive = archive();

        DemoFinalHandoffReportPackageArchiveEntity entity =
                DemoFinalHandoffReportPackageArchiveConvert.toEntity(archive);
        DemoFinalHandoffReportPackageArchiveVo convertedArchive =
                DemoFinalHandoffReportPackageArchiveConvert.toVo(entity);

        assertThat(entity.getId()).isEqualTo("final-handoff-package-archive-1");
        assertThat(entity.getStatus()).isEqualTo("READY");
        assertThat(entity.getDownloadReady()).isTrue();
        assertThat(entity.getLatestArchiveId()).isEqualTo("handoff-archive-1");
        assertThat(entity.getLatestSessionId()).isEqualTo("demo-session-20260624T003000Z");
        assertThat(entity.getLatestDeliveryReceiptId()).isEqualTo("delivery-receipt-1");
        assertThat(entity.getTaskCertificateArchiveId()).isEqualTo("task-evidence-certificate-archive-1");
        assertThat(entity.getTaskCertificateReady()).isTrue();
        assertThat(entity.getReadinessChecksJson()).contains("Finalization: READY");
        assertThat(entity.getRequiredAttachmentsJson()).contains("Finalization report");
        assertThat(entity.getPreSendChecksJson()).contains("Confirm no handoff share checklist warnings remain.");
        assertThat(entity.getEvidenceNotesJson()).contains("Latest delivery receipt delivery-receipt-1 is fresh.");
        assertThat(entity.getSourceReportsJson()).contains("Handoff finalization");
        assertThat(convertedArchive).isEqualTo(archive);
    }

    @Test
    void returns_empty_lists_when_json_columns_are_blank() {
        DemoFinalHandoffReportPackageArchiveEntity entity =
                DemoFinalHandoffReportPackageArchiveConvert.toEntity(archive());
        entity.setReadinessChecksJson(null);
        entity.setRequiredAttachmentsJson("");
        entity.setPreSendChecksJson(null);
        entity.setEvidenceNotesJson("");
        entity.setSourceReportsJson(null);

        DemoFinalHandoffReportPackageArchiveVo convertedArchive =
                DemoFinalHandoffReportPackageArchiveConvert.toVo(entity);

        assertThat(convertedArchive.readinessChecks()).isEmpty();
        assertThat(convertedArchive.requiredAttachments()).isEmpty();
        assertThat(convertedArchive.preSendChecks()).isEmpty();
        assertThat(convertedArchive.evidenceNotes()).isEmpty();
        assertThat(convertedArchive.sourceReports()).isEmpty();
    }

    private static DemoFinalHandoffReportPackageArchiveVo archive() {
        return new DemoFinalHandoffReportPackageArchiveVo(
                "final-handoff-package-archive-1",
                DemoReadinessStatus.READY,
                true,
                "Final demo handoff report package is ready to deliver.",
                "Download this final handoff report package and attach the listed evidence files.",
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                "delivery-receipt-1",
                "task-evidence-certificate-archive-1",
                true,
                List.of("Finalization: READY"),
                List.of("Finalization report"),
                List.of("Confirm no handoff share checklist warnings remain."),
                List.of("Latest delivery receipt delivery-receipt-1 is fresh."),
                List.of("Handoff finalization"),
                "# PatchPilot Final Demo Handoff Report Package",
                Instant.parse("2026-06-28T11:00:00Z"),
                Instant.parse("2026-06-28T11:30:00Z")
        );
    }
}
