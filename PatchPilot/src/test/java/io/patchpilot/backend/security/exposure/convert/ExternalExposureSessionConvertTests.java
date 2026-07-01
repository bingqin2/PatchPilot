package io.patchpilot.backend.security.exposure.convert;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureSessionVo;
import io.patchpilot.backend.security.exposure.domain.entity.ExternalExposureSessionEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalExposureSessionConvertTests {

    @Test
    void should_convert_session_to_entity_and_back() {
        ExternalExposureSessionVo session = session();

        ExternalExposureSessionEntity entity = ExternalExposureSessionConvert.toEntity(session);
        ExternalExposureSessionVo converted = ExternalExposureSessionConvert.toVo(entity);

        assertThat(entity.getId()).isEqualTo("exposure-session-1");
        assertThat(entity.getStatus()).isEqualTo("ACTIVE");
        assertThat(entity.getPublicUrl()).isEqualTo("https://demo.trycloudflare.com");
        assertThat(entity.getWebhookUrl()).isEqualTo("https://demo.trycloudflare.com/api/github/webhook");
        assertThat(entity.getExpectedShutdownAt()).isEqualTo(Instant.parse("2026-07-01T17:00:00Z"));
        assertThat(entity.getLinkedReadinessArchiveId()).isEqualTo("exposure-archive-1");
        assertThat(entity.getReport()).contains("# PatchPilot External Exposure Session");
        assertThat(converted).isEqualTo(session);
    }

    private static ExternalExposureSessionVo session() {
        return new ExternalExposureSessionVo(
                "exposure-session-1",
                "ACTIVE",
                "https://demo.trycloudflare.com",
                "https://demo.trycloudflare.com/api/github/webhook",
                "Live GitHub webhook smoke test",
                "bingqin2",
                Instant.parse("2026-07-01T17:00:00Z"),
                "Keep terminal visible during test.",
                "READY",
                "exposure-archive-1",
                Instant.parse("2026-07-01T15:00:00Z"),
                null,
                null,
                null,
                "# PatchPilot External Exposure Session"
        );
    }
}
