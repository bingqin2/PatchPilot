package io.patchpilot.backend.security.exposure;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureOperatorHandoffChecklistArchiveVo;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureOperatorHandoffChecklistVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/security/external-exposure-operator-handoff-checklist")
@RequiredArgsConstructor
public class ExternalExposureOperatorHandoffChecklistController {

    private final ExternalExposureOperatorHandoffChecklistService checklistService;
    private final ExternalExposureOperatorHandoffChecklistArchiveService archiveService;

    @GetMapping
    public ApiResponse<ExternalExposureOperatorHandoffChecklistVo> getChecklist() {
        return ApiResponse.ok(checklistService.getChecklist());
    }

    @GetMapping(value = "/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadReport() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"patchpilot-external-exposure-operator-handoff-checklist.md\"")
                .header(HttpHeaders.CONTENT_TYPE, "text/markdown;charset=UTF-8")
                .body(checklistService.getChecklist().markdownReport());
    }

    @PostMapping("/archives")
    public ApiResponse<ExternalExposureOperatorHandoffChecklistArchiveVo> archiveChecklist() {
        return ApiResponse.ok(archiveService.archiveCurrentChecklist());
    }

    @GetMapping("/archives")
    public ApiResponse<List<ExternalExposureOperatorHandoffChecklistArchiveVo>> listArchives() {
        return ApiResponse.ok(archiveService.listRecentArchives());
    }

    @GetMapping(value = "/archives/{archiveId}/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadArchiveReport(@PathVariable String archiveId) {
        return archiveService.findArchive(archiveId)
                .map(archive -> markdownAttachment(
                        "patchpilot-external-exposure-operator-handoff-checklist-" + safeFilenamePart(archive.id()) + ".md",
                        archive.report()
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
