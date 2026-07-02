package io.patchpilot.backend.demo;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.demo.domain.DemoLiveDemoEvidenceBundleArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoEvidenceBundleVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/demo/live-demo-evidence-bundle")
@RequiredArgsConstructor
public class DemoLiveDemoEvidenceBundleController {

    private final DemoLiveDemoEvidenceBundleService service;
    private final DemoLiveDemoEvidenceBundleArchiveService archiveService;

    @GetMapping
    public ApiResponse<DemoLiveDemoEvidenceBundleVo> getBundle() {
        return ApiResponse.ok(service.createBundle());
    }

    @GetMapping("/report/download")
    public ResponseEntity<byte[]> downloadReport() {
        byte[] body = service.createBundle().markdownReport().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/markdown;charset=UTF-8"))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("patchpilot-live-demo-evidence-bundle.md", StandardCharsets.UTF_8)
                                .build()
                                .toString()
                )
                .body(body);
    }

    @PostMapping("/archives")
    public ApiResponse<DemoLiveDemoEvidenceBundleArchiveVo> archiveBundle() {
        return ApiResponse.ok(archiveService.archiveBundle());
    }

    @GetMapping("/archives")
    public ApiResponse<List<DemoLiveDemoEvidenceBundleArchiveVo>> listArchives() {
        return ApiResponse.ok(archiveService.listRecentArchives());
    }

    @GetMapping("/archives/{archiveId}/report/download")
    public ResponseEntity<byte[]> downloadArchiveReport(@PathVariable String archiveId) {
        return archiveService.findArchive(archiveId)
                .map(archive -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("text/markdown;charset=UTF-8"))
                        .header(
                                HttpHeaders.CONTENT_DISPOSITION,
                                ContentDisposition.attachment()
                                        .filename(
                                                "patchpilot-live-demo-evidence-bundle-archive-" + archive.id() + ".md",
                                                StandardCharsets.UTF_8
                                        )
                                        .build()
                                        .toString()
                        )
                        .body(archive.report().getBytes(StandardCharsets.UTF_8)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
