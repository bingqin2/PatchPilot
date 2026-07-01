package io.patchpilot.backend.demo;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerOutcomeCloseoutArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerOutcomeCloseoutCommand;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerOutcomeCloseoutVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
@RequestMapping("/api/demo/live-trigger-outcome-closeout")
@RequiredArgsConstructor
public class DemoLiveTriggerOutcomeCloseoutController {

    private final DemoLiveTriggerOutcomeCloseoutService closeoutService;
    private final DemoLiveTriggerOutcomeCloseoutArchiveService archiveService;

    @PostMapping
    public ResponseEntity<ApiResponse<DemoLiveTriggerOutcomeCloseoutVo>> createCloseout(
            @RequestBody DemoLiveTriggerOutcomeCloseoutRequestDto request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(closeoutService.createCloseout(toCommand(request))));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(exception.getMessage()));
        }
    }

    @PostMapping("/report/download")
    public ResponseEntity<byte[]> downloadCloseoutReport(
            @RequestBody DemoLiveTriggerOutcomeCloseoutRequestDto request
    ) {
        DemoLiveTriggerOutcomeCloseoutVo closeout = closeoutService.createCloseout(toCommand(request));
        byte[] body = closeout.markdownReport().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/markdown;charset=UTF-8"))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("patchpilot-live-trigger-outcome-closeout.md", StandardCharsets.UTF_8)
                                .build()
                                .toString()
                )
                .body(body);
    }

    @PostMapping("/archives")
    public ResponseEntity<ApiResponse<DemoLiveTriggerOutcomeCloseoutArchiveVo>> archiveCloseout(
            @RequestBody DemoLiveTriggerOutcomeCloseoutRequestDto request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(archiveService.archiveCloseout(toCommand(request))));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(exception.getMessage()));
        }
    }

    @GetMapping("/archives")
    public ApiResponse<List<DemoLiveTriggerOutcomeCloseoutArchiveVo>> listArchives() {
        return ApiResponse.ok(archiveService.listRecentArchives());
    }

    @GetMapping("/archives/{archiveId}/report/download")
    public ResponseEntity<byte[]> downloadArchivedCloseoutReport(@PathVariable String archiveId) {
        DemoLiveTriggerOutcomeCloseoutArchiveVo archive = archiveService.findArchive(archiveId)
                .orElseThrow(() -> new IllegalArgumentException("Outcome closeout archive not found: " + archiveId));
        byte[] body = archive.report().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/markdown;charset=UTF-8"))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(
                                        "patchpilot-live-trigger-outcome-closeout-archive-%s.md".formatted(safeFilenamePart(archiveId)),
                                        StandardCharsets.UTF_8
                                )
                                .build()
                                .toString()
                )
                .body(body);
    }

    private static DemoLiveTriggerOutcomeCloseoutCommand toCommand(DemoLiveTriggerOutcomeCloseoutRequestDto request) {
        if (request == null) {
            throw new IllegalArgumentException("request body is required");
        }
        String repositoryOwner = requiredText(request.repositoryOwner(), "repositoryOwner must not be blank");
        String repositoryName = requiredText(request.repositoryName(), "repositoryName must not be blank");
        if (request.issueNumber() == null || request.issueNumber() < 1) {
            throw new IllegalArgumentException("issueNumber must be positive");
        }
        String triggerUser = requiredText(request.triggerUser(), "triggerUser must not be blank");
        String triggerComment = requiredText(request.triggerComment(), "triggerComment must not be blank");
        if (!triggerComment.equals("/agent fix") && !triggerComment.startsWith("/agent fix ")) {
            throw new IllegalArgumentException("triggerComment must start with /agent fix");
        }
        String launchPackageArchiveId = request.launchPackageArchiveId() == null
                || request.launchPackageArchiveId().isBlank()
                ? null
                : request.launchPackageArchiveId().trim();
        return new DemoLiveTriggerOutcomeCloseoutCommand(
                repositoryOwner,
                repositoryName,
                request.issueNumber(),
                triggerUser,
                triggerComment,
                launchPackageArchiveId
        );
    }

    private static String requiredText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private static String safeFilenamePart(String value) {
        return value == null ? "unknown" : value.replaceAll("[^a-zA-Z0-9._-]", "-");
    }
}
