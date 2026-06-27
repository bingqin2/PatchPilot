package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoHandoffShareDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareInstructionsVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import io.patchpilot.backend.demo.service.impl.InMemoryDemoHandoffShareDeliveryReceiptRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DemoHandoffShareDeliveryReceiptServiceTests {

    @Test
    void should_record_handoff_share_delivery_receipt_from_ready_instructions() {
        InMemoryDemoHandoffShareDeliveryReceiptRepository repository =
                new InMemoryDemoHandoffShareDeliveryReceiptRepository();
        DemoHandoffShareDeliveryReceiptService service = new DemoHandoffShareDeliveryReceiptService(
                DemoHandoffShareDeliveryReceiptServiceTests::readyInstructions,
                repository,
                Clock.fixed(Instant.parse("2026-06-24T06:10:00Z"), ZoneOffset.UTC),
                () -> "delivery-receipt-1"
        );

        DemoHandoffShareDeliveryReceiptVo receipt = service.recordDeliveryReceipt(
                new DemoHandoffShareDeliveryReceiptRequestDto(
                        "email",
                        "maintainer@example.com",
                        "local-operator",
                        "Sent after the demo review.",
                        Instant.parse("2026-06-24T06:05:00Z")
                )
        );

        assertThat(receipt.id()).isEqualTo("delivery-receipt-1");
        assertThat(receipt.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(receipt.handoffArchiveId()).isEqualTo("handoff-archive-1");
        assertThat(receipt.sessionId()).isEqualTo("demo-session-20260624T003000Z");
        assertThat(receipt.deliveryChannel()).isEqualTo("email");
        assertThat(receipt.deliveryTarget()).isEqualTo("maintainer@example.com");
        assertThat(receipt.operator()).isEqualTo("local-operator");
        assertThat(receipt.notes()).isEqualTo("Sent after the demo review.");
        assertThat(receipt.messageSubject()).isEqualTo("PatchPilot demo handoff: demo-session-20260624T003000Z");
        assertThat(receipt.deliveredAt()).isEqualTo(Instant.parse("2026-06-24T06:05:00Z"));
        assertThat(receipt.createdAt()).isEqualTo(Instant.parse("2026-06-24T06:10:00Z"));
        assertThat(receipt.markdownReport())
                .contains("# PatchPilot Demo Handoff Share Delivery Receipt")
                .contains("- Handoff archive: `handoff-archive-1`")
                .contains("- Delivery channel: `email`")
                .contains("does not send messages, create tasks, call the model, mutate Git, or write to GitHub");
        assertThat(service.listRecentReceipts()).containsExactly(receipt);
        assertThat(service.findReceipt("delivery-receipt-1")).contains(receipt);
    }

    @Test
    void should_reject_delivery_receipt_when_share_instructions_are_not_send_ready() {
        InMemoryDemoHandoffShareDeliveryReceiptRepository repository =
                new InMemoryDemoHandoffShareDeliveryReceiptRepository();
        DemoHandoffShareDeliveryReceiptService service = new DemoHandoffShareDeliveryReceiptService(
                DemoHandoffShareDeliveryReceiptServiceTests::blockedInstructions,
                repository,
                Clock.fixed(Instant.parse("2026-06-24T06:10:00Z"), ZoneOffset.UTC),
                () -> "delivery-receipt-1"
        );

        assertThatThrownBy(() -> service.recordDeliveryReceipt(new DemoHandoffShareDeliveryReceiptRequestDto(
                "email",
                "maintainer@example.com",
                "local-operator",
                "Sent after the demo review.",
                Instant.parse("2026-06-24T06:05:00Z")
        )))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("handoff share instructions are not send-ready");
        assertThat(service.listRecentReceipts()).isEmpty();
    }

    private static DemoHandoffShareInstructionsVo readyInstructions() {
        return new DemoHandoffShareInstructionsVo(
                DemoReadinessStatus.READY,
                true,
                "Share the current handoff package with repository maintainers and demo reviewers.",
                "Send the prepared handoff message with all required attachments.",
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                List.of("Repository owner or maintainer", "Demo reviewer"),
                List.of(
                        "Handoff package archive handoff-archive-1",
                        "Handoff package archive summary",
                        "Handoff share checklist",
                        "Handoff share center report"
                ),
                List.of("Confirm no handoff share checklist warnings remain."),
                "PatchPilot demo handoff: demo-session-20260624T003000Z",
                "The PatchPilot demo handoff package is ready to share.",
                "# PatchPilot Demo Handoff Share Instructions\n\n- Latest archive: `handoff-archive-1`\n- Latest session: `demo-session-20260624T003000Z`",
                Instant.parse("2026-06-24T05:45:00Z")
        );
    }

    private static DemoHandoffShareInstructionsVo blockedInstructions() {
        return new DemoHandoffShareInstructionsVo(
                DemoReadinessStatus.BLOCKED,
                false,
                "The handoff package is not ready to send.",
                "Archive a demo handoff package before recording delivery.",
                null,
                null,
                List.of("Repository owner or maintainer"),
                List.of("Create a handoff package archive before attaching final handoff evidence."),
                List.of("Do not send the handoff message until the share center reports READY."),
                "PatchPilot demo handoff: not ready",
                "The PatchPilot demo handoff package is not ready to send yet.",
                "# PatchPilot Demo Handoff Share Instructions\n\n- Status: `BLOCKED`",
                Instant.parse("2026-06-24T05:45:00Z")
        );
    }
}
