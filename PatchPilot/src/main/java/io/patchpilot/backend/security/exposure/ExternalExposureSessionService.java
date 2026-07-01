package io.patchpilot.backend.security.exposure;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureHandoffPackageVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureSessionCloseRequestDto;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureSessionCreateRequestDto;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureSessionVo;
import io.patchpilot.backend.security.exposure.service.ExternalExposureSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class ExternalExposureSessionService {

    private static final int MAX_SESSIONS = 50;

    private final Supplier<ExternalExposureHandoffPackageVo> handoffPackageSupplier;
    private final ExternalExposureSessionRepository sessionRepository;
    private final Clock clock;
    private final Supplier<String> idSupplier;

    @Autowired
    public ExternalExposureSessionService(
            ExternalExposureHandoffPackageService handoffPackageService,
            ExternalExposureSessionRepository sessionRepository
    ) {
        this(
                handoffPackageService::getHandoffPackage,
                sessionRepository,
                Clock.systemUTC(),
                () -> UUID.randomUUID().toString()
        );
    }

    ExternalExposureSessionService(
            Supplier<ExternalExposureHandoffPackageVo> handoffPackageSupplier,
            ExternalExposureSessionRepository sessionRepository,
            Clock clock,
            Supplier<String> idSupplier
    ) {
        this.handoffPackageSupplier = handoffPackageSupplier;
        this.sessionRepository = sessionRepository;
        this.clock = clock;
        this.idSupplier = idSupplier;
    }

    public ExternalExposureSessionVo startSession(ExternalExposureSessionCreateRequestDto request) {
        ExternalExposureHandoffPackageVo handoffPackage = handoffPackageSupplier.get();
        if (!handoffReady(handoffPackage)) {
            throw new IllegalStateException("external exposure handoff package is not ready");
        }

        Instant startedAt = Instant.now(clock);
        String publicUrl = requiredText(request.publicUrl(), "publicUrl");
        String webhookUrl = requiredText(request.webhookUrl(), "webhookUrl");
        String purpose = requiredText(request.purpose(), "purpose");
        String operator = requiredText(request.operator(), "operator");
        String notes = optionalText(request.notes());
        String id = idSupplier.get();

        ExternalExposureSessionVo session = new ExternalExposureSessionVo(
                id,
                "ACTIVE",
                publicUrl,
                webhookUrl,
                purpose,
                operator,
                request.expectedShutdownAt(),
                notes,
                handoffPackage.status(),
                handoffPackage.latestArchiveId(),
                startedAt,
                null,
                null,
                null,
                formatMarkdownReport(
                        id,
                        "ACTIVE",
                        publicUrl,
                        webhookUrl,
                        purpose,
                        operator,
                        request.expectedShutdownAt(),
                        notes,
                        handoffPackage.status(),
                        handoffPackage.latestArchiveId(),
                        startedAt,
                        null,
                        null,
                        null,
                        handoffPackage
                )
        );
        return sessionRepository.save(session);
    }

    public ExternalExposureSessionVo closeSession(String sessionId, ExternalExposureSessionCloseRequestDto request) {
        ExternalExposureSessionVo session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("external exposure session not found"));
        String closedBy = requiredText(request.closedBy(), "closedBy");
        Instant closedAt = request.closedAt() == null ? Instant.now(clock) : request.closedAt();
        String closeNotes = optionalText(request.closeNotes());
        ExternalExposureHandoffPackageVo handoffPackage = handoffPackageSupplier.get();

        ExternalExposureSessionVo closed = new ExternalExposureSessionVo(
                session.id(),
                "CLOSED",
                session.publicUrl(),
                session.webhookUrl(),
                session.purpose(),
                session.operator(),
                session.expectedShutdownAt(),
                session.notes(),
                session.linkedHandoffStatus(),
                session.linkedReadinessArchiveId(),
                session.startedAt(),
                closedBy,
                closedAt,
                closeNotes,
                formatMarkdownReport(
                        session.id(),
                        "CLOSED",
                        session.publicUrl(),
                        session.webhookUrl(),
                        session.purpose(),
                        session.operator(),
                        session.expectedShutdownAt(),
                        session.notes(),
                        session.linkedHandoffStatus(),
                        session.linkedReadinessArchiveId(),
                        session.startedAt(),
                        closedBy,
                        closedAt,
                        closeNotes,
                        handoffPackage
                )
        );
        return sessionRepository.save(closed);
    }

    public List<ExternalExposureSessionVo> listRecentSessions() {
        return sessionRepository.listRecentSessions(MAX_SESSIONS);
    }

    public Optional<ExternalExposureSessionVo> findSession(String sessionId) {
        return sessionRepository.findById(sessionId);
    }

    private static boolean handoffReady(ExternalExposureHandoffPackageVo handoffPackage) {
        return handoffPackage != null
                && "READY".equals(handoffPackage.status())
                && handoffPackage.handoffReady()
                && hasText(handoffPackage.latestArchiveId());
    }

    private static String formatMarkdownReport(
            String id,
            String status,
            String publicUrl,
            String webhookUrl,
            String purpose,
            String operator,
            Instant expectedShutdownAt,
            String notes,
            String linkedHandoffStatus,
            String linkedReadinessArchiveId,
            Instant startedAt,
            String closedBy,
            Instant closedAt,
            String closeNotes,
            ExternalExposureHandoffPackageVo handoffPackage
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("# PatchPilot External Exposure Session\n\n");
        builder.append("- Session id: `").append(id).append("`\n");
        builder.append("- Status: `").append(status).append("`\n");
        builder.append("- Public URL: ").append(publicUrl).append('\n');
        builder.append("- GitHub webhook URL: ").append(webhookUrl).append('\n');
        builder.append("- Purpose: ").append(purpose).append('\n');
        builder.append("- Operator: `").append(operator).append("`\n");
        builder.append("- Expected shutdown at: `").append(valueOrNone(expectedShutdownAt)).append("`\n");
        builder.append("- Linked handoff status: `").append(valueOrNone(linkedHandoffStatus)).append("`\n");
        builder.append("- Linked readiness archive: `").append(valueOrNone(linkedReadinessArchiveId)).append("`\n");
        builder.append("- Started at: `").append(startedAt).append("`\n");
        builder.append("- Closed by: `").append(valueOrNone(closedBy)).append("`\n");
        builder.append("- Closed at: `").append(valueOrNone(closedAt)).append("`\n\n");
        builder.append("## Session Notes\n\n");
        builder.append(hasText(notes) ? notes : "No start notes recorded.").append("\n\n");
        builder.append("## Close Notes\n\n");
        builder.append(hasText(closeNotes) ? closeNotes : "Session is not closed.").append("\n\n");
        builder.append("## Handoff Evidence\n\n");
        if (handoffPackage == null) {
            builder.append("No handoff package snapshot available when this report was generated.\n\n");
        } else {
            builder.append("- Current handoff status: `").append(handoffPackage.status()).append("`\n");
            builder.append("- Handoff summary: ").append(handoffPackage.summary()).append('\n');
            builder.append("- Handoff next action: ").append(handoffPackage.nextAction()).append("\n\n");
        }
        builder.append("## Side-Effect Contract\n\n");
        builder.append("POST /api/security/external-exposure-sessions records local exposure-session evidence only: ");
        builder.append("it does not probe the public URL, mutate GitHub webhook settings, create tasks, call the model, ");
        builder.append("run tests, mutate Git, open Pull Requests, archive readiness records, or write GitHub comments.\n");
        return builder.toString();
    }

    private static String requiredText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return value.trim();
    }

    private static String optionalText(String value) {
        return value == null ? "" : value.trim();
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static String valueOrNone(String value) {
        return hasText(value) ? value.trim() : "none";
    }

    private static String valueOrNone(Instant value) {
        return value == null ? "none" : value.toString();
    }
}
