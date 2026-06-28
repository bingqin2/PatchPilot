package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceShareDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceShareFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceSharePackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoFinalAcceptanceShareDeliveryReceiptRepository;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoFinalAcceptanceSharePackageArchiveRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoFinalAcceptanceShareFinalizationServiceTests {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-06-29T03:30:00Z"), ZoneOffset.UTC);

    @Test
    void should_report_ready_when_latest_final_acceptance_archive_has_fresh_delivery_receipt() {
        InMemoryDemoFinalAcceptanceSharePackageArchiveRepository archiveRepository = archiveRepository(sendReadyArchive());
        InMemoryDemoFinalAcceptanceShareDeliveryReceiptRepository receiptRepository =
                receiptRepository(freshReceipt("final-acceptance-share-package-archive-1", "task-1"));
        DemoFinalAcceptanceShareFinalizationService service = new DemoFinalAcceptanceShareFinalizationService(
                archiveRepository,
                receiptRepository,
                CLOCK
        );

        DemoFinalAcceptanceShareFinalizationVo finalization = service.getFinalizationGate();

        assertThat(finalization.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(finalization.finalized()).isTrue();
        assertThat(finalization.summary())
                .isEqualTo("Final demo acceptance share package is finalized with a fresh delivery receipt.");
        assertThat(finalization.nextAction())
                .isEqualTo("Use the finalization report as the external-review acceptance delivery record.");
        assertThat(finalization.latestArchiveId()).isEqualTo("final-acceptance-share-package-archive-1");
        assertThat(finalization.latestTaskId()).isEqualTo("task-1");
        assertThat(finalization.latestDeliveryReceiptId()).isEqualTo("final-acceptance-delivery-receipt-1");
        assertThat(finalization.deliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(finalization.deliveryReceiptFresh()).isTrue();
        assertThat(finalization.generatedAt()).isEqualTo(Instant.parse("2026-06-29T03:30:00Z"));
        assertThat(finalization.checks())
                .extracting(check -> check.name() + ":" + check.status())
                .containsExactly(
                        "Final acceptance package archive:READY",
                        "Delivery receipt freshness:READY",
                        "Final acceptance delivery evidence:READY"
                );
        assertThat(finalization.evidenceNotes()).contains(
                "Latest final acceptance archive final-acceptance-share-package-archive-1 is send-ready.",
                "Latest delivery receipt final-acceptance-delivery-receipt-1 is fresh for final-acceptance-share-package-archive-1."
        );
        assertThat(finalization.markdownReport())
                .contains("# PatchPilot Final Demo Acceptance Share Finalization Gate")
                .contains("- Status: `READY`")
                .contains("- Finalized: `true`")
                .contains("- Latest delivery receipt: `final-acceptance-delivery-receipt-1`")
                .contains("GET /api/demo/final-acceptance-share-finalization is read-only");
    }

    @Test
    void should_need_attention_when_send_ready_archive_has_no_delivery_receipt() {
        DemoFinalAcceptanceShareFinalizationService service = new DemoFinalAcceptanceShareFinalizationService(
                archiveRepository(sendReadyArchive()),
                new InMemoryDemoFinalAcceptanceShareDeliveryReceiptRepository(),
                CLOCK
        );

        DemoFinalAcceptanceShareFinalizationVo finalization = service.getFinalizationGate();

        assertThat(finalization.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(finalization.finalized()).isFalse();
        assertThat(finalization.summary())
                .isEqualTo("Final demo acceptance share package is send-ready but final delivery evidence is not current.");
        assertThat(finalization.nextAction())
                .isEqualTo("Send the current final acceptance share package, record a delivery receipt, then download the finalization report.");
        assertThat(finalization.deliveryReceiptFreshness()).isEqualTo("MISSING");
        assertThat(finalization.checks())
                .extracting(check -> check.name() + ":" + check.status())
                .containsExactly(
                        "Final acceptance package archive:READY",
                        "Delivery receipt freshness:NEEDS_ATTENTION",
                        "Final acceptance delivery evidence:NEEDS_ATTENTION"
                );
        assertThat(finalization.markdownReport())
                .contains("- Delivery receipt freshness: `MISSING`")
                .contains("Record a final acceptance share delivery receipt after sending the package.");
    }

    @Test
    void should_need_attention_when_latest_delivery_receipt_belongs_to_previous_archive() {
        DemoFinalAcceptanceShareFinalizationService service = new DemoFinalAcceptanceShareFinalizationService(
                archiveRepository(sendReadyArchive()),
                receiptRepository(freshReceipt("old-final-acceptance-archive", "task-1")),
                CLOCK
        );

        DemoFinalAcceptanceShareFinalizationVo finalization = service.getFinalizationGate();

        assertThat(finalization.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(finalization.finalized()).isFalse();
        assertThat(finalization.latestDeliveryReceiptId()).isEqualTo("final-acceptance-delivery-receipt-1");
        assertThat(finalization.deliveryReceiptFreshness()).isEqualTo("STALE");
        assertThat(finalization.deliveryReceiptFresh()).isFalse();
        assertThat(finalization.nextAction())
                .isEqualTo("Record a new delivery receipt for final acceptance share package archive final-acceptance-share-package-archive-1, then download the finalization report.");
        assertThat(finalization.markdownReport())
                .contains("- Delivery receipt freshness: `STALE`")
                .contains("Latest delivery receipt final-acceptance-delivery-receipt-1 belongs to old-final-acceptance-archive/task-1");
    }

    @Test
    void should_block_when_latest_archive_is_not_send_ready() {
        DemoFinalAcceptanceShareFinalizationService service = new DemoFinalAcceptanceShareFinalizationService(
                archiveRepository(notReadyArchive()),
                new InMemoryDemoFinalAcceptanceShareDeliveryReceiptRepository(),
                CLOCK
        );

        DemoFinalAcceptanceShareFinalizationVo finalization = service.getFinalizationGate();

        assertThat(finalization.status()).isEqualTo(DemoReadinessStatus.BLOCKED);
        assertThat(finalization.finalized()).isFalse();
        assertThat(finalization.summary())
                .isEqualTo("Final demo acceptance share finalization is blocked because the latest package archive is not send-ready.");
        assertThat(finalization.nextAction()).isEqualTo("Fix final acceptance evidence before sending.");
        assertThat(finalization.checks())
                .extracting(check -> check.name() + ":" + check.status())
                .containsExactly(
                        "Final acceptance package archive:BLOCKED",
                        "Delivery receipt freshness:NEEDS_ATTENTION",
                        "Final acceptance delivery evidence:BLOCKED"
                );
    }

    private static InMemoryDemoFinalAcceptanceSharePackageArchiveRepository archiveRepository(
            DemoFinalAcceptanceSharePackageArchiveVo archive
    ) {
        InMemoryDemoFinalAcceptanceSharePackageArchiveRepository repository =
                new InMemoryDemoFinalAcceptanceSharePackageArchiveRepository();
        repository.save(archive);
        return repository;
    }

    private static InMemoryDemoFinalAcceptanceShareDeliveryReceiptRepository receiptRepository(
            DemoFinalAcceptanceShareDeliveryReceiptVo receipt
    ) {
        InMemoryDemoFinalAcceptanceShareDeliveryReceiptRepository repository =
                new InMemoryDemoFinalAcceptanceShareDeliveryReceiptRepository();
        repository.save(receipt);
        return repository;
    }

    private static DemoFinalAcceptanceShareDeliveryReceiptVo freshReceipt(String archiveId, String taskId) {
        return new DemoFinalAcceptanceShareDeliveryReceiptVo(
                "final-acceptance-delivery-receipt-1",
                DemoReadinessStatus.READY,
                archiveId,
                taskId,
                "email",
                "reviewer@example.com",
                "local-operator",
                "Sent final acceptance share package to the reviewer.",
                "PatchPilot final demo acceptance: task-1",
                Instant.parse("2026-06-29T03:05:00Z"),
                Instant.parse("2026-06-29T03:10:00Z"),
                "# PatchPilot Final Demo Acceptance Share Delivery Receipt"
        );
    }

    private static DemoFinalAcceptanceSharePackageArchiveVo sendReadyArchive() {
        return archive(true, DemoReadinessStatus.READY);
    }

    private static DemoFinalAcceptanceSharePackageArchiveVo notReadyArchive() {
        return archive(false, DemoReadinessStatus.BLOCKED);
    }

    private static DemoFinalAcceptanceSharePackageArchiveVo archive(boolean sendReady, DemoReadinessStatus status) {
        return new DemoFinalAcceptanceSharePackageArchiveVo(
                "final-acceptance-share-package-archive-1",
                status,
                sendReady,
                sendReady
                        ? "PatchPilot final demo acceptance package is ready to send."
                        : "PatchPilot final demo acceptance package is blocked.",
                sendReady
                        ? "Send the prepared final acceptance message with all required attachments."
                        : "Fix final acceptance evidence before sending.",
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
