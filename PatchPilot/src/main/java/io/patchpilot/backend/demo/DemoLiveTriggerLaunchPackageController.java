package io.patchpilot.backend.demo;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerLaunchPackageArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerLaunchPackageCommand;
import io.patchpilot.backend.demo.domain.DemoLiveTriggerLaunchPackageVo;
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
@RequestMapping("/api/demo/live-trigger-launch-package")
@RequiredArgsConstructor
public class DemoLiveTriggerLaunchPackageController {

    private final DemoLiveTriggerLaunchPackageService launchPackageService;
    private final DemoLiveTriggerLaunchPackageArchiveService archiveService;

    @PostMapping
    public ResponseEntity<ApiResponse<DemoLiveTriggerLaunchPackageVo>> createPackage(
            @RequestBody DemoLiveTriggerLaunchPackageRequestDto request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(launchPackageService.createPackage(toCommand(request))));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(exception.getMessage()));
        }
    }

    @PostMapping("/report/download")
    public ResponseEntity<byte[]> downloadPackageReport(
            @RequestBody DemoLiveTriggerLaunchPackageRequestDto request
    ) {
        DemoLiveTriggerLaunchPackageVo launchPackage = launchPackageService.createPackage(toCommand(request));
        byte[] body = launchPackage.markdownReport().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/markdown;charset=UTF-8"))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("patchpilot-live-trigger-launch-package.md", StandardCharsets.UTF_8)
                                .build()
                                .toString()
                )
                .body(body);
    }

    @PostMapping("/archives")
    public ResponseEntity<ApiResponse<DemoLiveTriggerLaunchPackageArchiveVo>> archivePackage(
            @RequestBody DemoLiveTriggerLaunchPackageRequestDto request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(archiveService.archivePackage(toCommand(request))));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(exception.getMessage()));
        }
    }

    @GetMapping("/archives")
    public ApiResponse<List<DemoLiveTriggerLaunchPackageArchiveVo>> listArchives() {
        return ApiResponse.ok(archiveService.listRecentArchives());
    }

    @GetMapping(value = "/archives/{archiveId}/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadArchiveReport(@PathVariable String archiveId) {
        return archiveService.findArchive(archiveId)
                .map(archive -> ResponseEntity.ok()
                        .header(
                                HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"patchpilot-live-trigger-launch-package-archive-"
                                        + safeFilenamePart(archive.id())
                                        + ".md\""
                        )
                        .header(HttpHeaders.CONTENT_TYPE, "text/markdown;charset=UTF-8")
                        .body(archive.report()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private static DemoLiveTriggerLaunchPackageCommand toCommand(DemoLiveTriggerLaunchPackageRequestDto request) {
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
        return new DemoLiveTriggerLaunchPackageCommand(
                repositoryOwner,
                repositoryName,
                request.issueNumber(),
                triggerUser,
                triggerComment
        );
    }

    private static String requiredText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private static String safeFilenamePart(String value) {
        String sanitized = new String(value.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8)
                .replaceAll("[^A-Za-z0-9._-]", "-");
        return sanitized.isBlank() ? "report" : sanitized;
    }
}
