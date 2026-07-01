package io.patchpilot.backend.security.exposure;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureSessionCloseRequestDto;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureSessionCreateRequestDto;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureSessionVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/security/external-exposure-sessions")
@RequiredArgsConstructor
public class ExternalExposureSessionController {

    private final ExternalExposureSessionService sessionService;

    @PostMapping
    public ResponseEntity<ApiResponse<ExternalExposureSessionVo>> startExternalExposureSession(
            @RequestBody ExternalExposureSessionCreateRequestDto request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(sessionService.startSession(request)));
        } catch (IllegalArgumentException | IllegalStateException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(exception.getMessage()));
        }
    }

    @PostMapping("/{sessionId}/close")
    public ResponseEntity<ApiResponse<ExternalExposureSessionVo>> closeExternalExposureSession(
            @PathVariable String sessionId,
            @RequestBody ExternalExposureSessionCloseRequestDto request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(sessionService.closeSession(sessionId, request)));
        } catch (IllegalArgumentException | IllegalStateException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(exception.getMessage()));
        }
    }

    @GetMapping
    public ApiResponse<List<ExternalExposureSessionVo>> listExternalExposureSessions() {
        return ApiResponse.ok(sessionService.listRecentSessions());
    }

    @GetMapping(value = "/{sessionId}/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadExternalExposureSessionReport(@PathVariable String sessionId) {
        return sessionService.findSession(sessionId)
                .map(session -> markdownAttachment(
                        "patchpilot-external-exposure-session-" + safeFilenamePart(session.id()) + ".md",
                        session.markdownReport()
                ))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private static ResponseEntity<String> markdownAttachment(String filename, String markdown) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CONTENT_TYPE, "text/markdown;charset=UTF-8")
                .body(markdown);
    }

    private static String safeFilenamePart(String value) {
        String sanitized = new String(value.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8)
                .replaceAll("[^A-Za-z0-9._-]", "-");
        return sanitized.isBlank() ? "report" : sanitized;
    }
}
