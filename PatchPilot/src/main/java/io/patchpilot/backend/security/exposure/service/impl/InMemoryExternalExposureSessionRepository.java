package io.patchpilot.backend.security.exposure.service.impl;

import io.patchpilot.backend.security.exposure.domain.ExternalExposureSessionVo;
import io.patchpilot.backend.security.exposure.service.ExternalExposureSessionRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
@Profile("default")
public class InMemoryExternalExposureSessionRepository implements ExternalExposureSessionRepository {

    private static final int MAX_SESSIONS = 50;

    private final List<ExternalExposureSessionVo> sessions = new CopyOnWriteArrayList<>();

    @Override
    public ExternalExposureSessionVo save(ExternalExposureSessionVo session) {
        sessions.removeIf(existingSession -> existingSession.id().equals(session.id()));
        sessions.add(0, session);
        trimSessions();
        return session;
    }

    @Override
    public List<ExternalExposureSessionVo> listRecentSessions(int limit) {
        return sessions.stream()
                .limit(limit)
                .toList();
    }

    @Override
    public Optional<ExternalExposureSessionVo> findById(String sessionId) {
        return sessions.stream()
                .filter(session -> session.id().equals(sessionId))
                .findFirst();
    }

    private void trimSessions() {
        while (sessions.size() > MAX_SESSIONS) {
            sessions.remove(sessions.size() - 1);
        }
    }
}
