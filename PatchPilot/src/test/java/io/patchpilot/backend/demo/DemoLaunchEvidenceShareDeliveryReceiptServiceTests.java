package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceShareCenterVo;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceShareDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoLaunchEvidenceShareDeliveryReceiptRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DemoLaunchEvidenceShareDeliveryReceiptServiceTests {

    @Test
    void should_record_launch_evidence_delivery_receipt_from_share_ready_center() {
        InMemoryDemoLaunchEvidenceShareDeliveryReceiptRepository repository =
                new InMemoryDemoLaunchEvidenceShareDeliveryReceiptRepository();
        DemoLaunchEvidenceShareDeliveryReceiptService service = new DemoLaunchEvidenceShareDeliveryReceiptService(
                DemoLaunchEvidenceShareDeliveryReceiptServiceTests::shareReadyCenter,
                repository,
                Clock.fixed(Instant.parse("2026-06-28T06:10:00Z"), ZoneOffset.UTC),
                () -> "launch-delivery-receipt-1"
        );

        DemoLaunchEvidenceShareDeliveryReceiptVo receipt = service.recordDeliveryReceipt(
                new DemoLaunchEvidenceShareDeliveryReceiptRequestDto(
                        "email",
                        "reviewer@example.com",
                        "local-operator",
                        "Sent final launch evidence after the smoke demo.",
                        Instant.parse("2026-06-28T06:05:00Z")
                )
        );

        assertThat(receipt.id()).isEqualTo("launch-delivery-receipt-1");
        assertThat(receipt.status()).isEqualTo("READY");
        assertThat(receipt.launchEvidenceArchiveId()).isEqualTo("launch-evidence-archive-1");
        assertThat(receipt.sessionId()).isEqualTo("demo-session-20260624T003000Z");
        assertThat(receipt.deliveryChannel()).isEqualTo("email");
        assertThat(receipt.deliveryTarget()).isEqualTo("reviewer@example.com");
        assertThat(receipt.operator()).isEqualTo("local-operator");
        assertThat(receipt.notes()).isEqualTo("Sent final launch evidence after the smoke demo.");
        assertThat(receipt.messageSubject()).isEqualTo("PatchPilot demo launch evidence: demo-session-20260624T003000Z");
        assertThat(receipt.deliveredAt()).isEqualTo(Instant.parse("2026-06-28T06:05:00Z"));
        assertThat(receipt.createdAt()).isEqualTo(Instant.parse("2026-06-28T06:10:00Z"));
        assertThat(receipt.markdownReport())
                .contains("# PatchPilot Demo Launch Evidence Delivery Receipt")
                .contains("- Launch evidence archive: `launch-evidence-archive-1`")
                .contains("- Delivery channel: `email`")
                .contains("POST /api/demo/launch-evidence-share-delivery-receipts records local evidence only")
                .contains("does not send messages, create tasks, call the model, run tests, mutate Git, or write to GitHub");
        assertThat(service.listRecentReceipts()).containsExactly(receipt);
        assertThat(service.findReceipt("launch-delivery-receipt-1")).contains(receipt);
    }

    @Test
    void should_reject_delivery_receipt_when_launch_evidence_is_not_share_ready() {
        InMemoryDemoLaunchEvidenceShareDeliveryReceiptRepository repository =
                new InMemoryDemoLaunchEvidenceShareDeliveryReceiptRepository();
        DemoLaunchEvidenceShareDeliveryReceiptService service = new DemoLaunchEvidenceShareDeliveryReceiptService(
                DemoLaunchEvidenceShareDeliveryReceiptServiceTests::notReadyCenter,
                repository,
                Clock.fixed(Instant.parse("2026-06-28T06:10:00Z"), ZoneOffset.UTC),
                () -> "launch-delivery-receipt-1"
        );

        assertThatThrownBy(() -> service.recordDeliveryReceipt(new DemoLaunchEvidenceShareDeliveryReceiptRequestDto(
                "email",
                "reviewer@example.com",
                "local-operator",
                "Sent final launch evidence after the smoke demo.",
                Instant.parse("2026-06-28T06:05:00Z")
        )))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("launch evidence share center is not share-ready");
        assertThat(service.listRecentReceipts()).isEmpty();
    }

    private static DemoLaunchEvidenceShareCenterVo shareReadyCenter() {
        return new DemoLaunchEvidenceShareCenterVo(
                "READY",
                true,
                "Latest archived launch evidence package is READY and can be shared.",
                "Download the archived launch evidence package and share it with reviewers.",
                1,
                "launch-evidence-archive-1",
                "demo-session-20260624T003000Z",
                "2026-06-28T02:30:00Z",
                "task-1",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "delivery-1",
                "evaluation-run-2",
                DemoReadinessStatus.READY,
                true,
                "final-handoff-report-package-archive-1",
                null,
                null,
                null,
                null,
                false,
                "MISSING",
                false,
                "No delivery receipt has been recorded for the current launch evidence package.",
                List.of("Download launch evidence package archive launch-evidence-archive-1."),
                List.of("Latest launch evidence archive status is READY."),
                "# PatchPilot Demo Launch Evidence Share Center",
                Instant.parse("2026-06-28T02:45:00Z")
        );
    }

    private static DemoLaunchEvidenceShareCenterVo notReadyCenter() {
        return new DemoLaunchEvidenceShareCenterVo(
                "NO_ARCHIVE",
                false,
                "No archived launch evidence package is available for sharing.",
                "Archive a final demo launch evidence package after a completed live run before sharing launch evidence.",
                0,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                DemoReadinessStatus.NEEDS_ATTENTION,
                false,
                null,
                null,
                null,
                null,
                null,
                false,
                "MISSING",
                false,
                "No delivery receipt has been recorded for the current launch evidence package.",
                List.of("Archive a demo launch evidence package before downloading final launch evidence."),
                List.of("No launch evidence archive has been captured yet."),
                "# PatchPilot Demo Launch Evidence Share Center",
                Instant.parse("2026-06-28T02:45:00Z")
        );
    }
}
