package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoHandoffDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoHandoffPackageVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DemoLiveDemoHandoffDeliveryReceiptServiceTests {

    @Test
    void should_record_delivery_receipt_for_ready_live_demo_handoff_package() {
        InMemoryDemoLiveDemoHandoffDeliveryReceiptRepository repository =
                new InMemoryDemoLiveDemoHandoffDeliveryReceiptRepository();
        DemoLiveDemoHandoffDeliveryReceiptService service = new DemoLiveDemoHandoffDeliveryReceiptService(
                () -> readyPackage(),
                repository,
                () -> "live-demo-handoff-delivery-receipt-1",
                () -> Instant.parse("2026-07-02T05:00:00Z")
        );

        DemoLiveDemoHandoffDeliveryReceiptVo receipt = service.recordDeliveryReceipt(validRequest());

        assertThat(receipt.id()).isEqualTo("live-demo-handoff-delivery-receipt-1");
        assertThat(receipt.status()).isEqualTo("READY");
        assertThat(receipt.handoffPackageStatus()).isEqualTo("READY");
        assertThat(receipt.evidenceBundleArchiveId()).isEqualTo("live-demo-evidence-bundle-archive-1");
        assertThat(receipt.repository()).isEqualTo("bingqin2/PatchPilot");
        assertThat(receipt.issueUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/issues/1");
        assertThat(receipt.taskId()).isEqualTo("task-1");
        assertThat(receipt.pullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(receipt.deliveryChannel()).isEqualTo("github-comment");
        assertThat(receipt.deliveryTarget()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(receipt.operator()).isEqualTo("local-operator");
        assertThat(receipt.notes()).isEqualTo("Sent the live demo handoff package to the reviewer.");
        assertThat(receipt.deliveredAt()).isEqualTo(Instant.parse("2026-07-02T04:55:00Z"));
        assertThat(receipt.createdAt()).isEqualTo(Instant.parse("2026-07-02T05:00:00Z"));
        assertThat(receipt.markdownReport())
                .contains("# PatchPilot Live Demo Handoff Delivery Receipt")
                .contains("live-demo-evidence-bundle-archive-1")
                .contains("https://github.com/bingqin2/PatchPilot/pull/42")
                .contains("POST /api/demo/live-demo-handoff-package/delivery-receipts records local evidence only");
        assertThat(repository.listRecentReceipts(20)).containsExactly(receipt);
        assertThat(service.findReceipt("live-demo-handoff-delivery-receipt-1")).contains(receipt);
    }

    @Test
    void should_reject_delivery_receipt_when_handoff_package_is_not_ready() {
        DemoLiveDemoHandoffDeliveryReceiptService service = new DemoLiveDemoHandoffDeliveryReceiptService(
                () -> blockedPackage(),
                new InMemoryDemoLiveDemoHandoffDeliveryReceiptRepository(),
                () -> "live-demo-handoff-delivery-receipt-1",
                () -> Instant.parse("2026-07-02T05:00:00Z")
        );

        assertThatThrownBy(() -> service.recordDeliveryReceipt(validRequest()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("live demo handoff package is not ready for delivery");
    }

    @Test
    void should_require_delivery_fields() {
        DemoLiveDemoHandoffDeliveryReceiptService service = new DemoLiveDemoHandoffDeliveryReceiptService(
                () -> readyPackage(),
                new InMemoryDemoLiveDemoHandoffDeliveryReceiptRepository(),
                () -> "live-demo-handoff-delivery-receipt-1",
                () -> Instant.parse("2026-07-02T05:00:00Z")
        );

        assertThatThrownBy(() -> service.recordDeliveryReceipt(
                new DemoLiveDemoHandoffDeliveryReceiptRequestDto(
                        "github-comment",
                        " ",
                        "local-operator",
                        "Sent package.",
                        Instant.parse("2026-07-02T04:55:00Z")
                )
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("deliveryTarget is required");
    }

    static DemoLiveDemoHandoffPackageVo readyPackage() {
        return new DemoLiveDemoHandoffPackageVo(
                "READY",
                true,
                "live-demo-evidence-bundle-archive-1",
                "bingqin2/PatchPilot",
                1,
                "https://github.com/bingqin2/PatchPilot/issues/1",
                "bingqin2",
                "/agent fix touch docs/live-package.md",
                "task-1",
                "COMPLETED",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "delivery-1",
                "Live demo handoff package is ready for reviewer handoff.",
                List.of("Open the Pull Request and review the files changed."),
                List.of("Share this handoff package and archived evidence report with the reviewer."),
                List.of("Launch package archive launch-package-archive-1 is ready."),
                "read-only live demo handoff package",
                Instant.parse("2026-07-02T04:00:00Z"),
                "# PatchPilot Live Demo Handoff Package"
        );
    }

    static DemoLiveDemoHandoffPackageVo blockedPackage() {
        return new DemoLiveDemoHandoffPackageVo(
                "BLOCKED",
                false,
                null,
                null,
                0,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                "PatchPilot is missing a live demo evidence bundle archive for reviewer handoff.",
                List.of(),
                List.of("Archive a live demo evidence bundle before preparing the final reviewer handoff package."),
                List.of(),
                "read-only live demo handoff package",
                Instant.parse("2026-07-02T04:00:00Z"),
                "# PatchPilot Live Demo Handoff Package"
        );
    }

    private static DemoLiveDemoHandoffDeliveryReceiptRequestDto validRequest() {
        return new DemoLiveDemoHandoffDeliveryReceiptRequestDto(
                "github-comment",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "local-operator",
                "Sent the live demo handoff package to the reviewer.",
                Instant.parse("2026-07-02T04:55:00Z")
        );
    }
}
