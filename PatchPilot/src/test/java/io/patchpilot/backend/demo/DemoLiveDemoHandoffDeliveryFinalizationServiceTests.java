package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoHandoffDeliveryFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoHandoffDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoHandoffPackageVo;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DemoLiveDemoHandoffDeliveryFinalizationServiceTests {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-07-02T06:00:00Z"), ZoneOffset.UTC);

    @Test
    void finalizes_live_demo_handoff_delivery_when_latest_receipt_matches_current_package() {
        DemoLiveDemoHandoffDeliveryFinalizationService service =
                new DemoLiveDemoHandoffDeliveryFinalizationService(
                        DemoLiveDemoHandoffDeliveryReceiptServiceTests::readyPackage,
                        () -> List.of(freshReceipt()),
                        CLOCK
                );

        DemoLiveDemoHandoffDeliveryFinalizationVo finalization =
                service.getFinalizationGate();

        assertThat(finalization.status()).isEqualTo("READY");
        assertThat(finalization.finalized()).isTrue();
        assertThat(finalization.summary()).isEqualTo(
                "Live demo handoff delivery is finalized with a fresh delivery receipt."
        );
        assertThat(finalization.nextAction()).isEqualTo(
                "Use this finalization report as the live demo reviewer handoff completion proof."
        );
        assertThat(finalization.latestDeliveryReceiptId())
                .isEqualTo("live-demo-handoff-delivery-receipt-1");
        assertThat(finalization.evidenceBundleArchiveId())
                .isEqualTo("live-demo-evidence-bundle-archive-1");
        assertThat(finalization.repository()).isEqualTo("bingqin2/PatchPilot");
        assertThat(finalization.issueUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/issues/1");
        assertThat(finalization.taskId()).isEqualTo("task-1");
        assertThat(finalization.pullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(finalization.latestDeliveryTarget()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(finalization.latestDeliveryChannel()).isEqualTo("github-comment");
        assertThat(finalization.deliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(finalization.deliveryReceiptFresh()).isTrue();
        assertThat(finalization.deliveryReceiptFreshnessSummary()).isEqualTo(
                "Latest live demo handoff delivery receipt matches the current handoff package."
        );
        assertThat(finalization.checks())
                .extracting(DemoLiveDemoHandoffDeliveryFinalizationVo.Check::name)
                .containsExactly("Live demo handoff package", "Live demo handoff delivery receipt");
        assertThat(finalization.evidenceNotes()).contains(
                "Live demo handoff package is ready.",
                "Live demo handoff delivery receipt live-demo-handoff-delivery-receipt-1 is fresh."
        );
        assertThat(finalization.downloadActions()).contains(
                "Download live demo handoff delivery finalization report.",
                "Download live demo handoff delivery receipt live-demo-handoff-delivery-receipt-1."
        );
        assertThat(finalization.markdownReport())
                .contains("# PatchPilot Live Demo Handoff Delivery Finalization")
                .contains("- Status: `READY`")
                .contains("- Delivery receipt freshness: `FRESH`")
                .contains("does not send messages, write to GitHub, create tasks");
        assertThat(finalization.generatedAt()).isEqualTo(Instant.parse("2026-07-02T06:00:00Z"));
    }

    @Test
    void needs_attention_when_ready_package_has_no_delivery_receipt() {
        DemoLiveDemoHandoffDeliveryFinalizationService service =
                new DemoLiveDemoHandoffDeliveryFinalizationService(
                        DemoLiveDemoHandoffDeliveryReceiptServiceTests::readyPackage,
                        List::of,
                        CLOCK
                );

        DemoLiveDemoHandoffDeliveryFinalizationVo finalization =
                service.getFinalizationGate();

        assertThat(finalization.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(finalization.finalized()).isFalse();
        assertThat(finalization.deliveryReceiptFreshness()).isEqualTo("MISSING");
        assertThat(finalization.deliveryReceiptFresh()).isFalse();
        assertThat(finalization.latestDeliveryReceiptId()).isNull();
        assertThat(finalization.nextAction()).isEqualTo(
                "Send the live demo handoff package, record a delivery receipt, then download this finalization report."
        );
    }

    @Test
    void needs_attention_when_latest_receipt_is_stale_for_current_package() {
        DemoLiveDemoHandoffDeliveryFinalizationService service =
                new DemoLiveDemoHandoffDeliveryFinalizationService(
                        DemoLiveDemoHandoffDeliveryReceiptServiceTests::readyPackage,
                        () -> List.of(staleReceipt()),
                        CLOCK
                );

        DemoLiveDemoHandoffDeliveryFinalizationVo finalization =
                service.getFinalizationGate();

        assertThat(finalization.status()).isEqualTo("NEEDS_ATTENTION");
        assertThat(finalization.finalized()).isFalse();
        assertThat(finalization.deliveryReceiptFreshness()).isEqualTo("STALE");
        assertThat(finalization.nextAction()).isEqualTo(
                "Record a new live demo handoff delivery receipt for evidence bundle archive live-demo-evidence-bundle-archive-1."
        );
        assertThat(finalization.evidenceNotes()).contains(
                "Latest live demo handoff delivery receipt live-demo-handoff-delivery-receipt-old is stale."
        );
    }

    @Test
    void blocks_finalization_when_package_is_not_ready() {
        DemoLiveDemoHandoffDeliveryFinalizationService service =
                new DemoLiveDemoHandoffDeliveryFinalizationService(
                        DemoLiveDemoHandoffDeliveryReceiptServiceTests::blockedPackage,
                        () -> List.of(freshReceipt()),
                        CLOCK
                );

        DemoLiveDemoHandoffDeliveryFinalizationVo finalization =
                service.getFinalizationGate();

        assertThat(finalization.status()).isEqualTo("BLOCKED");
        assertThat(finalization.finalized()).isFalse();
        assertThat(finalization.deliveryReceiptFreshness()).isEqualTo("BLOCKED");
        assertThat(finalization.summary()).isEqualTo(
                "Live demo handoff delivery finalization is blocked because the handoff package is not ready."
        );
    }

    private static DemoLiveDemoHandoffDeliveryReceiptVo freshReceipt() {
        return receipt(
                "live-demo-handoff-delivery-receipt-1",
                "live-demo-evidence-bundle-archive-1",
                "bingqin2/PatchPilot",
                1,
                "https://github.com/bingqin2/PatchPilot/issues/1",
                "task-1",
                "COMPLETED",
                "https://github.com/bingqin2/PatchPilot/pull/42"
        );
    }

    private static DemoLiveDemoHandoffDeliveryReceiptVo staleReceipt() {
        return receipt(
                "live-demo-handoff-delivery-receipt-old",
                "live-demo-evidence-bundle-archive-old",
                "bingqin2/PatchPilot",
                1,
                "https://github.com/bingqin2/PatchPilot/issues/1",
                "task-1",
                "COMPLETED",
                "https://github.com/bingqin2/PatchPilot/pull/42"
        );
    }

    private static DemoLiveDemoHandoffDeliveryReceiptVo receipt(
            String id,
            String evidenceBundleArchiveId,
            String repository,
            long issueNumber,
            String issueUrl,
            String taskId,
            String taskStatus,
            String pullRequestUrl
    ) {
        return new DemoLiveDemoHandoffDeliveryReceiptVo(
                id,
                "READY",
                "READY",
                evidenceBundleArchiveId,
                repository,
                issueNumber,
                issueUrl,
                "bingqin2",
                "/agent fix touch docs/live-package.md",
                taskId,
                taskStatus,
                pullRequestUrl,
                "delivery-1",
                "Live demo handoff package delivery receipt is recorded.",
                "github-comment",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                "local-operator",
                "Sent live demo handoff package.",
                Instant.parse("2026-07-02T05:30:00Z"),
                Instant.parse("2026-07-02T05:35:00Z"),
                "# PatchPilot Live Demo Handoff Delivery Receipt"
        );
    }
}
