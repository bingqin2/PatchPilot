package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoHandoffFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoHandoffShareCenterVo;
import io.patchpilot.backend.demo.domain.DemoReadinessStatus;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DemoHandoffFinalizationServiceTests {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-06-24T06:00:00Z"), ZoneOffset.UTC);

    private final DemoHandoffShareCenterService shareCenterService = mock(DemoHandoffShareCenterService.class);
    private final DemoHandoffFinalizationService service = new DemoHandoffFinalizationService(
            shareCenterService,
            CLOCK
    );

    @Test
    void should_report_ready_when_current_handoff_package_has_fresh_delivery_receipt() {
        when(shareCenterService.getShareCenter()).thenReturn(freshDeliveredShareCenter());

        DemoHandoffFinalizationVo finalization = service.getFinalizationGate();

        assertThat(finalization.status()).isEqualTo(DemoReadinessStatus.READY);
        assertThat(finalization.finalized()).isTrue();
        assertThat(finalization.summary())
                .isEqualTo("Demo handoff is finalized with a fresh delivery receipt for the current archive.");
        assertThat(finalization.nextAction())
                .isEqualTo("Use the finalization report as the post-demo delivery acceptance record.");
        assertThat(finalization.latestArchiveId()).isEqualTo("handoff-archive-1");
        assertThat(finalization.latestSessionId()).isEqualTo("demo-session-20260624T003000Z");
        assertThat(finalization.latestDeliveryReceiptId()).isEqualTo("receipt-1");
        assertThat(finalization.deliveryReceiptFreshness()).isEqualTo("FRESH");
        assertThat(finalization.deliveryReceiptFresh()).isTrue();
        assertThat(finalization.generatedAt()).isEqualTo(Instant.parse("2026-06-24T06:00:00Z"));
        assertThat(finalization.checks())
                .extracting(check -> check.name() + ":" + check.status())
                .containsExactly(
                        "Handoff package share readiness:READY",
                        "Delivery receipt freshness:READY",
                        "Task evidence certificate:READY",
                        "Final acceptance evidence:READY"
                );
        assertThat(finalization.evidenceNotes()).contains(
                "Share center status is READY.",
                "Task evidence certificate task-evidence-certificate-archive-1 is ready for task task-2.",
                "Latest delivery receipt receipt-1 is fresh for handoff-archive-1/demo-session-20260624T003000Z.",
                "Finalization report can be downloaded as the acceptance record."
        );
        assertThat(finalization.markdownReport())
                .contains("# PatchPilot Demo Handoff Finalization Gate")
                .contains("- Status: `READY`")
                .contains("- Finalized: `true`")
                .contains("- Latest archive: `handoff-archive-1`")
                .contains("- Latest delivery receipt: `receipt-1`")
                .contains("Task evidence certificate: `READY` - Task evidence acceptance certificate is attached to the final handoff package.")
                .contains("GET /api/demo/handoff-finalization is read-only");
    }

    @Test
    void should_need_attention_when_share_package_is_ready_but_delivery_receipt_is_missing() {
        when(shareCenterService.getShareCenter()).thenReturn(missingReceiptShareCenter());

        DemoHandoffFinalizationVo finalization = service.getFinalizationGate();

        assertThat(finalization.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(finalization.finalized()).isFalse();
        assertThat(finalization.summary())
                .isEqualTo("Demo handoff package is send-ready but final delivery evidence is not current.");
        assertThat(finalization.nextAction())
                .isEqualTo("Send the current handoff package, record a delivery receipt, then download the finalization report.");
        assertThat(finalization.deliveryReceiptFreshness()).isEqualTo("MISSING");
        assertThat(finalization.checks())
                .extracting(check -> check.name() + ":" + check.status())
                .containsExactly(
                        "Handoff package share readiness:READY",
                        "Delivery receipt freshness:NEEDS_ATTENTION",
                        "Task evidence certificate:READY",
                        "Final acceptance evidence:NEEDS_ATTENTION"
                );
        assertThat(finalization.evidenceNotes()).contains(
                "No fresh delivery receipt is available for handoff-archive-1/demo-session-20260624T003000Z."
        );
        assertThat(finalization.markdownReport())
                .contains("- Delivery receipt freshness: `MISSING`")
                .contains("Record a handoff share delivery receipt after sending the package.");
    }

    @Test
    void should_need_attention_when_latest_delivery_receipt_is_stale() {
        when(shareCenterService.getShareCenter()).thenReturn(staleReceiptShareCenter());

        DemoHandoffFinalizationVo finalization = service.getFinalizationGate();

        assertThat(finalization.status()).isEqualTo(DemoReadinessStatus.NEEDS_ATTENTION);
        assertThat(finalization.finalized()).isFalse();
        assertThat(finalization.deliveryReceiptFreshness()).isEqualTo("STALE");
        assertThat(finalization.latestDeliveryReceiptId()).isEqualTo("receipt-old");
        assertThat(finalization.nextAction())
                .isEqualTo("Record a new delivery receipt for archive handoff-archive-1, then download the finalization report.");
        assertThat(finalization.checks())
                .extracting(check -> check.name() + ":" + check.status())
                .containsExactly(
                        "Handoff package share readiness:READY",
                        "Delivery receipt freshness:NEEDS_ATTENTION",
                        "Task evidence certificate:READY",
                        "Final acceptance evidence:NEEDS_ATTENTION"
                );
        assertThat(finalization.markdownReport())
                .contains("- Delivery receipt freshness: `STALE`")
                .contains("Latest delivery receipt receipt-old belongs to old-handoff-archive/old-session");
    }

    @Test
    void should_block_finalization_when_handoff_package_is_blocked_from_sharing() {
        when(shareCenterService.getShareCenter()).thenReturn(blockedShareCenter());

        DemoHandoffFinalizationVo finalization = service.getFinalizationGate();

        assertThat(finalization.status()).isEqualTo(DemoReadinessStatus.BLOCKED);
        assertThat(finalization.finalized()).isFalse();
        assertThat(finalization.summary())
                .isEqualTo("Demo handoff finalization is blocked before delivery can be accepted.");
        assertThat(finalization.nextAction())
                .isEqualTo("Resolve blocked handoff readiness checks before sharing the package.");
        assertThat(finalization.checks())
                .extracting(check -> check.name() + ":" + check.status())
                .containsExactly(
                        "Handoff package share readiness:BLOCKED",
                        "Delivery receipt freshness:NEEDS_ATTENTION",
                        "Task evidence certificate:READY",
                        "Final acceptance evidence:BLOCKED"
                );
        assertThat(finalization.markdownReport())
                .contains("- Status: `BLOCKED`")
                .contains("- Finalized: `false`");
    }

    private static DemoHandoffShareCenterVo freshDeliveredShareCenter() {
        return new DemoHandoffShareCenterVo(
                DemoReadinessStatus.READY,
                true,
                "Post-demo handoff package is ready to share.",
                "Download the package, archive summary, and share checklist before sending handoff evidence.",
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                "2026-06-24T04:00:00Z",
                "receipt-1",
                "Demo reviewer",
                "email",
                "2026-06-24T05:20:00Z",
                true,
                "FRESH",
                true,
                "Latest delivery receipt matches the current handoff archive and session.",
                List.of("Download handoff share delivery receipt receipt-1."),
                List.of("Latest package archive status is READY."),
                "# PatchPilot Demo Handoff Share Center",
                Instant.parse("2026-06-24T05:30:00Z")
        );
    }

    private static DemoHandoffShareCenterVo missingReceiptShareCenter() {
        return new DemoHandoffShareCenterVo(
                DemoReadinessStatus.READY,
                true,
                "Post-demo handoff package is ready to share.",
                "Download the package, send the prepared handoff message, then record a delivery receipt.",
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                "2026-06-24T04:00:00Z",
                null,
                null,
                null,
                null,
                false,
                "MISSING",
                false,
                "No delivery receipt has been recorded for the current handoff package.",
                List.of("Record a handoff share delivery receipt after sending the package."),
                List.of("No handoff share delivery receipt has been recorded yet."),
                "# PatchPilot Demo Handoff Share Center",
                Instant.parse("2026-06-24T05:30:00Z")
        );
    }

    private static DemoHandoffShareCenterVo staleReceiptShareCenter() {
        return new DemoHandoffShareCenterVo(
                DemoReadinessStatus.READY,
                true,
                "Post-demo handoff package is ready to share.",
                "Send the current handoff package and record a new delivery receipt for archive handoff-archive-1.",
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                "2026-06-24T04:00:00Z",
                "receipt-old",
                "Demo reviewer",
                "email",
                "2026-06-24T05:20:00Z",
                true,
                "STALE",
                false,
                "Latest delivery receipt receipt-old belongs to old-handoff-archive/old-session, not current handoff-archive-1/demo-session-20260624T003000Z.",
                List.of("Record a new handoff share delivery receipt for archive handoff-archive-1."),
                List.of("Latest delivery receipt receipt-old belongs to old-handoff-archive/old-session, not current handoff-archive-1/demo-session-20260624T003000Z."),
                "# PatchPilot Demo Handoff Share Center",
                Instant.parse("2026-06-24T05:30:00Z")
        );
    }

    private static DemoHandoffShareCenterVo blockedShareCenter() {
        return new DemoHandoffShareCenterVo(
                DemoReadinessStatus.BLOCKED,
                false,
                "Post-demo handoff package is blocked from sharing.",
                "Resolve blocked handoff readiness checks before sharing the package.",
                "handoff-archive-1",
                "demo-session-20260624T003000Z",
                "2026-06-24T04:00:00Z",
                null,
                null,
                null,
                null,
                false,
                "MISSING",
                false,
                "No delivery receipt has been recorded for the current handoff package.",
                List.of("Resolve checklist warnings before sending the package."),
                List.of("Latest package archive status is BLOCKED."),
                "# PatchPilot Demo Handoff Share Center",
                Instant.parse("2026-06-24T05:30:00Z")
        );
    }
}
