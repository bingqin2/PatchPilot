package io.patchpilot.backend.demo;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.demo.domain.DemoLiveDemoHandoffPackageVo;
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
@RequestMapping("/api/demo/live-demo-handoff-package")
@RequiredArgsConstructor
public class DemoLiveDemoHandoffPackageController {

    private final DemoLiveDemoHandoffPackageService service;

    @GetMapping
    public ApiResponse<DemoLiveDemoHandoffPackageVo> getPackage() {
        return ApiResponse.ok(service.createPackage());
    }

    @GetMapping("/report/download")
    public ResponseEntity<byte[]> downloadReport() {
        byte[] body = service.createPackage().markdownReport().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/markdown;charset=UTF-8"))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("patchpilot-live-demo-handoff-package.md", StandardCharsets.UTF_8)
                                .build()
                                .toString()
                )
                .body(body);
    }
}
