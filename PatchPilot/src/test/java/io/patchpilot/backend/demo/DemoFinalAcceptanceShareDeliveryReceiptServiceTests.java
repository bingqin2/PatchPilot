package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceShareDeliveryReceiptVo;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DemoFinalAcceptanceShareDeliveryReceiptServiceTests {

    @Test
    void should_record_delivery_receipt_for_latest_send_ready_final_acceptance_share_package_archive() {
        InMemoryDemoFinalAcceptanceSharePackageArchiveRepository archiveRepository =
                new InMemoryDemoFinalAcceptanceSharePackageArchiveRepository();
        archiveRepository.save(sendReadyArchive());
        InMemoryDemoFinalAcceptanceShareDeliveryReceiptRepository receiptRepository =
                new InMemoryDemoFinalAcceptanceShareDeliveryReceiptRepository();
        DemoFinalAcceptanceShareDeliveryReceiptService service = new DemoFinalAcceptanceShareDeliveryReceiptService(
                archiveRepository,
                receiptRepository,
                Clock.fixed(Instant.parse("2026-06-29T03:10:00Z"), ZoneOffset.UTC),
                () -> "final-acceptance-delivery-receipt-1"
        );

        DemoFinalAcceptanceShareDeliveryReceiptVo receipt = service.recordDeliveryReceipt(
                new DemoFinalAcceptanceShareDeliveryReceiptRequestDto(
                        "email",
                        "reviewer@example.com",
                        "local-operator",
                        "Sent final acceptance share package to the reviewer.",
                        Instant.parse("2026-06-29T03:05:00Z")
                )
        );

        assertThat(receipt.id()).isEqualTo("final-acceptance-delivery-receipt-1");
        assertThat(receipt.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(receipt.finalAcceptanceSharePackageArchiveId()).isEqualTo("final-acceptance-share-package-archive-1");
        assertThat(receipt.latestTaskId()).isEqualTo("task-1");
        assertThat(receipt.deliveryChannel()).isEqualTo("email");
        assertThat(receipt.deliveryTarget()).isEqualTo("reviewer@example.com");
        assertThat(receipt.operator()).isEqualTo("local-operator");
        assertThat(receipt.notes()).isEqualTo("Sent final acceptance share package to the reviewer.");
        assertThat(receipt.messageSubject()).isEqualTo("PatchPilot final demo acceptance: task-1");
        assertThat(receipt.deliveredAt()).isEqualTo(Instant.parse("2026-06-29T03:05:00Z"));
        assertThat(receipt.createdAt()).isEqualTo(Instant.parse("2026-06-29T03:10:00Z"));
        assertThat(receipt.markdownReport())
                .contains("# PatchPilot Final Demo Acceptance Share Delivery Receipt")
                .contains("- Final acceptance package archive: `final-acceptance-share-package-archive-1`")
                .contains("- Delivery channel: `email`")
                .contains("POST /api/demo/final-acceptance-share-delivery-receipts records local evidence only")
                .contains("does not send messages, create tasks, call the model, run tests, mutate Git, record receipts, or write to GitHub");
        assertThat(service.listRecentReceipts()).containsExactly(receipt);
        assertThat(service.findReceipt("final-acceptance-delivery-receipt-1")).contains(receipt);
    }

    @Test
    void should_reject_delivery_receipt_when_no_final_acceptance_share_package_archive_exists() {
        DemoFinalAcceptanceShareDeliveryReceiptService service = new DemoFinalAcceptanceShareDeliveryReceiptService(
                new InMemoryDemoFinalAcceptanceSharePackageArchiveRepository(),
                new InMemoryDemoFinalAcceptanceShareDeliveryReceiptRepository(),
                Clock.fixed(Instant.parse("2026-06-29T03:10:00Z"), ZoneOffset.UTC),
                () -> "final-acceptance-delivery-receipt-1"
        );

        assertThatThrownBy(() -> service.recordDeliveryReceipt(validRequest()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("no final acceptance share package archive is available");
    }

    @Test
    void should_reject_delivery_receipt_when_latest_archive_is_not_send_ready() {
        InMemoryDemoFinalAcceptanceSharePackageArchiveRepository archiveRepository =
                new InMemoryDemoFinalAcceptanceSharePackageArchiveRepository();
        archiveRepository.save(notReadyArchive());
        DemoFinalAcceptanceShareDeliveryReceiptService service = new DemoFinalAcceptanceShareDeliveryReceiptService(
                archiveRepository,
                new InMemoryDemoFinalAcceptanceShareDeliveryReceiptRepository(),
                Clock.fixed(Instant.parse("2026-06-29T03:10:00Z"), ZoneOffset.UTC),
                () -> "final-acceptance-delivery-receipt-1"
        );

        assertThatThrownBy(() -> service.recordDeliveryReceipt(validRequest()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("final acceptance share package archive is not send-ready");
    }

    private static DemoFinalAcceptanceShareDeliveryReceiptRequestDto validRequest() {
        return new DemoFinalAcceptanceShareDeliveryReceiptRequestDto(
                "email",
                "reviewer@example.com",
                "local-operator",
                "Sent final acceptance share package to the reviewer.",
                Instant.parse("2026-06-29T03:05:00Z")
        );
    }

    private static DemoFinalAcceptanceSharePackageArchiveVo sendReadyArchive() {
        return archive(true, DemoReadinessStatus.READY);
    }

    private static DemoFinalAcceptanceSharePackageArchiveVo notReadyArchive() {
        return archive(false, DemoReadinessStatus.NEEDS_ATTENTION);
    }

    private static DemoFinalAcceptanceSharePackageArchiveVo archive(boolean sendReady, DemoReadinessStatus status) {
        return new DemoFinalAcceptanceSharePackageArchiveVo(
                "final-acceptance-share-package-archive-1",
                status,
                sendReady,
                sendReady
                        ? "PatchPilot final demo acceptance package is ready to send."
                        : "PatchPilot final demo acceptance package is not ready to send.",
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
