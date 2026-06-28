package io.patchpilot.backend.demo.convert;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceSharePackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.domain.entity.DemoFinalAcceptanceSharePackageArchiveEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalAcceptanceSharePackageArchiveConvertTests {

    @Test
    void converts_final_acceptance_share_package_archive_to_entity_and_back() {
        DemoFinalAcceptanceSharePackageArchiveVo archive = archive();

        DemoFinalAcceptanceSharePackageArchiveEntity entity =
                DemoFinalAcceptanceSharePackageArchiveConvert.toEntity(archive);
        DemoFinalAcceptanceSharePackageArchiveVo convertedArchive =
                DemoFinalAcceptanceSharePackageArchiveConvert.toVo(entity);

        assertThat(entity.getId()).isEqualTo("final-acceptance-share-package-archive-1");
        assertThat(entity.getStatus()).isEqualTo("READY");
        assertThat(entity.getSendReady()).isTrue();
        assertThat(entity.getLaunchCertificateArchiveId()).isEqualTo("launch-certificate-archive-1");
        assertThat(entity.getTaskCertificateArchiveId()).isEqualTo("task-evidence-certificate-archive-1");
        assertThat(entity.getLatestTaskId()).isEqualTo("task-1");
        assertThat(entity.getLatestPullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(entity.getRecommendedRecipientsJson()).contains("Repository owner or maintainer");
        assertThat(entity.getRequiredAttachmentsJson()).contains("Final demo acceptance summary report");
        assertThat(entity.getPreSendChecksJson()).contains("Confirm final demo acceptance status is READY and accepted.");
        assertThat(entity.getEvidenceNotesJson()).contains("Final acceptance status is READY.");
        assertThat(convertedArchive).isEqualTo(archive);
    }

    @Test
    void returns_empty_lists_when_json_columns_are_blank() {
        DemoFinalAcceptanceSharePackageArchiveEntity entity =
                DemoFinalAcceptanceSharePackageArchiveConvert.toEntity(archive());
        entity.setRecommendedRecipientsJson(null);
        entity.setRequiredAttachmentsJson("");
        entity.setPreSendChecksJson(null);
        entity.setEvidenceNotesJson("");

        DemoFinalAcceptanceSharePackageArchiveVo convertedArchive =
                DemoFinalAcceptanceSharePackageArchiveConvert.toVo(entity);

        assertThat(convertedArchive.recommendedRecipients()).isEmpty();
        assertThat(convertedArchive.requiredAttachments()).isEmpty();
        assertThat(convertedArchive.preSendChecks()).isEmpty();
        assertThat(convertedArchive.evidenceNotes()).isEmpty();
    }

    private static DemoFinalAcceptanceSharePackageArchiveVo archive() {
        return new DemoFinalAcceptanceSharePackageArchiveVo(
                "final-acceptance-share-package-archive-1",
                DemoReadinessStatus.READY,
                true,
                "PatchPilot final demo acceptance package is ready to send.",
                "Send the prepared final acceptance message with all required attachments.",
                "launch-certificate-archive-1",
                "task-evidence-certificate-archive-1",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                List.of("Repository owner or maintainer", "Demo reviewer"),
                List.of("Final demo acceptance summary report"),
                List.of("Confirm final demo acceptance status is READY and accepted."),
                "PatchPilot final demo acceptance: task-1",
                "PatchPilot final demo acceptance is ready for external review.",
                List.of("Final acceptance status is READY."),
                "POST /api/demo/final-acceptance-share-package/archives archives a read-only snapshot and does not create tasks, call the model, run tests, mutate Git, send messages, or write to GitHub.",
                "# PatchPilot Final Demo Acceptance Share Package",
                Instant.parse("2026-06-29T01:30:00Z"),
                Instant.parse("2026-06-29T02:00:00Z")
        );
    }
}
