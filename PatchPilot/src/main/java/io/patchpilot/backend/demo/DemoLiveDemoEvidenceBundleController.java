package io.patchpilot.backend.demo;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.demo.domain.DemoLiveDemoEvidenceBundleVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/demo/live-demo-evidence-bundle")
@RequiredArgsConstructor
public class DemoLiveDemoEvidenceBundleController {

    private final DemoLiveDemoEvidenceBundleService service;

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
}
