package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoReviewerDeliveryCenterArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptVo;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptServiceTests {

    @Test
    void should_record_delivery_receipt_for_latest_reviewer_delivery_center_archive() {
        InMemoryDemoLiveDemoReviewerDeliveryCenterArchiveRepository archiveRepository =
                new InMemoryDemoLiveDemoReviewerDeliveryCenterArchiveRepository();
        archiveRepository.save(readyArchive("archive-1"));
        DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptService service =
                new DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptService(
                        () -> archiveRepository.listRecentArchives(1),
                        new InMemoryDemoLiveDemoReviewerDeliveryCenterDeliveryReceiptRepository(),
                        () -> "receipt-1",
                        () -> Instant.parse("2026-07-02T14:00:00Z")
                );

        DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptVo receipt = service.recordDeliveryReceipt(
                new DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptRequestDto(
                        "github-comment",
                        "https://github.com/bingqin2/PatchPilot/pull/42#issuecomment-1",
                        "local-operator",
                        "Sent frozen delivery center to reviewer.",
                        Instant.parse("2026-07-02T13:55:00Z")
                )
        );

        assertThat(receipt.id()).isEqualTo("receipt-1");
        assertThat(receipt.status()).isEqualTo("READY");
        assertThat(receipt.reviewerDeliveryCenterArchiveId()).isEqualTo("archive-1");
        assertThat(receipt.repository()).isEqualTo("bingqin2/PatchPilot");
        assertThat(receipt.pullRequestUrl()).isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42");
        assertThat(receipt.deliveryChannel()).isEqualTo("github-comment");
        assertThat(receipt.deliveryTarget())
                .isEqualTo("https://github.com/bingqin2/PatchPilot/pull/42#issuecomment-1");
        assertThat(receipt.markdownReport())
                .contains("PatchPilot Live Demo Reviewer Delivery Center Delivery Receipt")
                .contains("archive-1")
                .contains("Sent frozen delivery center to reviewer.")
                .contains("records local reviewer delivery center delivery evidence only");
    }

    @Test
    void should_reject_delivery_receipt_when_no_ready_archive_exists() {
        DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptService service =
                new DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptService(
                        List::of,
                        new InMemoryDemoLiveDemoReviewerDeliveryCenterDeliveryReceiptRepository(),
                        () -> "receipt-1",
                        () -> Instant.parse("2026-07-02T14:00:00Z")
                );

        assertThatThrownBy(() -> service.recordDeliveryReceipt(
                new DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptRequestDto(
                        "github-comment",
                        "reviewer-thread",
                        "local-operator",
                        "",
                        null
                )
        ))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("READY reviewer delivery center archive is required");
    }

    @Test
    void should_keep_latest_twenty_delivery_receipts_first() {
        InMemoryDemoLiveDemoReviewerDeliveryCenterArchiveRepository archiveRepository =
                new InMemoryDemoLiveDemoReviewerDeliveryCenterArchiveRepository();
        archiveRepository.save(readyArchive("archive-1"));
        DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptService service =
                new DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptService(
                        () -> archiveRepository.listRecentArchives(1),
                        new InMemoryDemoLiveDemoReviewerDeliveryCenterDeliveryReceiptRepository(),
                        nextStringId(),
                        () -> Instant.parse("2026-07-02T14:00:00Z")
                );

        for (int index = 0; index < 25; index++) {
            service.recordDeliveryReceipt(new DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptRequestDto(
                    "github-comment",
                    "reviewer-thread-" + index,
                    "local-operator",
                    "",
                    null
            ));
        }

        List<DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptVo> receipts = service.listRecentReceipts();

        assertThat(receipts).hasSize(20);
        assertThat(receipts.get(0).id()).isEqualTo("25");
        assertThat(receipts.get(19).id()).isEqualTo("6");
    }

    private static DemoLiveDemoReviewerDeliveryCenterArchiveVo readyArchive(String id) {
        return new DemoLiveDemoReviewerDeliveryCenterArchiveVo(
                id,
                "READY",
                true,
                "PatchPilot live demo reviewer delivery center archive is ready.",
                "Send to reviewer.",
                "bingqin2/PatchPilot",
                1,
                "https://github.com/bingqin2/PatchPilot/issues/1",
                "task-1",
                "COMPLETED",
                "https://github.com/bingqin2/PatchPilot/pull/42",
                List.of(),
                List.of(),
                List.of(),
                List.of("Download live demo reviewer delivery center archive report."),
                "Archive creation writes only local evidence.",
                Instant.parse("2026-07-02T12:00:00Z"),
                Instant.parse("2026-07-02T13:00:00Z"),
                "# PatchPilot Live Demo Reviewer Delivery Center Archive"
        );
    }

    private static java.util.function.Supplier<String> nextStringId() {
        java.util.concurrent.atomic.AtomicInteger sequence = new java.util.concurrent.atomic.AtomicInteger();
        return () -> Integer.toString(sequence.incrementAndGet());
    }
}
