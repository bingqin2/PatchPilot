package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoHandoffDeliveryFinalizationArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoHandoffDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoHandoffPackageVo;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DemoLiveDemoHandoffDeliveryFinalizationArchiveServiceTests {

    private static final Clock FINALIZATION_CLOCK =
            Clock.fixed(Instant.parse("2026-07-02T06:00:00Z"), ZoneOffset.UTC);

    @Test
    void should_archive_ready_live_demo_handoff_delivery_finalization_for_stable_reviewer_proof() {
        DemoLiveDemoHandoffDeliveryFinalizationArchiveService service = service(
                DemoLiveDemoHandoffDeliveryReceiptServiceTests::readyPackage,
                () -> List.of(freshReceipt())
        );

        DemoLiveDemoHandoffDeliveryFinalizationArchiveVo archive = service.archiveFinalization();

        assertThat(archive.id()).isEqualTo("live-demo-handoff-delivery-finalization-archive-1");
        assertThat(archive.status()).isEqualTo("READY");
        assertThat(archive.finalized()).isTrue();
        assertThat(archive.latestDeliveryReceiptId()).isEqualTo("live-demo-handoff-delivery-receipt-1");
        assertThat(archive.evidenceBundleArchiveId()).isEqualTo("live-demo-evidence-bundle-archive-1");
        assertThat(archive.repository()).isEqualTo("bingqin2/PatchPilot");
        assertThat(archive.issueUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/issues/1");
        assertThat(archive.taskId()).isEqualTo("task-1");
        assertThat(archive.pullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(archive.deliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(archive.deliveryReceiptFresh()).isTrue();
        assertThat(archive.sideEffectContract()).contains("Archive creation writes only PatchPilot local archive records");
        assertThat(archive.finalizationGeneratedAt()).isEqualTo(Instant.parse("2026-07-02T06:00:00Z"));
        assertThat(archive.archivedAt()).isEqualTo(Instant.parse("2026-07-02T07:00:00Z"));
        assertThat(archive.report())
                .contains("# PatchPilot Live Demo Handoff Delivery Finalization Archive")
                .contains("live-demo-handoff-delivery-receipt-1")
                .contains("live-demo-evidence-bundle-archive-1");
        assertThat(service.listRecentArchives()).containsExactly(archive);
        assertThat(service.findArchive("live-demo-handoff-delivery-finalization-archive-1")).contains(archive);
    }

    @Test
    void should_reject_archive_when_finalization_is_not_ready() {
        DemoLiveDemoHandoffDeliveryFinalizationArchiveService service = service(
                DemoLiveDemoHandoffDeliveryReceiptServiceTests::readyPackage,
                List::of
        );

        assertThatThrownBy(service::archiveFinalization)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("READY live demo handoff delivery finalization is required");
    }

    @Test
    void should_keep_latest_twenty_archives_first() {
        AtomicInteger nextId = new AtomicInteger();
        DemoLiveDemoHandoffDeliveryFinalizationArchiveService service = service(
                DemoLiveDemoHandoffDeliveryReceiptServiceTests::readyPackage,
                () -> List.of(freshReceipt()),
                () -> "archive-" + nextId.incrementAndGet()
        );

        for (int index = 0; index < 21; index++) {
            service.archiveFinalization();
        }

        assertThat(service.listRecentArchives()).hasSize(20);
        assertThat(service.listRecentArchives().get(0).id()).isEqualTo("archive-21");
        assertThat(service.listRecentArchives())
                .extracting(DemoLiveDemoHandoffDeliveryFinalizationArchiveVo::id)
                .doesNotContain("archive-1");
    }

    private static DemoLiveDemoHandoffDeliveryFinalizationArchiveService service(
            java.util.function.Supplier<DemoLiveDemoHandoffPackageVo> packageSupplier,
            java.util.function.Supplier<List<DemoLiveDemoHandoffDeliveryReceiptVo>> receiptSupplier
    ) {
        return service(packageSupplier, receiptSupplier, () -> "live-demo-handoff-delivery-finalization-archive-1");
    }

    private static DemoLiveDemoHandoffDeliveryFinalizationArchiveService service(
            java.util.function.Supplier<DemoLiveDemoHandoffPackageVo> packageSupplier,
            java.util.function.Supplier<List<DemoLiveDemoHandoffDeliveryReceiptVo>> receiptSupplier,
            java.util.function.Supplier<String> idSupplier
    ) {
        DemoLiveDemoHandoffDeliveryFinalizationService finalizationService =
                new DemoLiveDemoHandoffDeliveryFinalizationService(
                        packageSupplier,
                        receiptSupplier,
                        FINALIZATION_CLOCK
                );
        return new DemoLiveDemoHandoffDeliveryFinalizationArchiveService(
                finalizationService,
                new InMemoryDemoLiveDemoHandoffDeliveryFinalizationArchiveRepository(),
                idSupplier,
                () -> Instant.parse("2026-07-02T07:00:00Z")
        );
    }

    private static DemoLiveDemoHandoffDeliveryReceiptVo freshReceipt() {
        return new DemoLiveDemoHandoffDeliveryReceiptVo(
                "live-demo-handoff-delivery-receipt-1",
                "READY",
                "READY",
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
