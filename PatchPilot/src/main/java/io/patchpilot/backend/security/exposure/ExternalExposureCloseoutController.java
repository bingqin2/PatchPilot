package io.patchpilot.backend.security.exposure;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureCloseoutVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/security/external-exposure-closeout")
@RequiredArgsConstructor
public class ExternalExposureCloseoutController {

    private final ExternalExposureCloseoutService closeoutService;

    @GetMapping
    public ApiResponse<ExternalExposureCloseoutVo> getExternalExposureCloseout() {
        return ApiResponse.ok(closeoutService.getCloseout());
    }

    @GetMapping(value = "/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadExternalExposureCloseoutReport() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"patchpilot-external-exposure-closeout.md\"")
                .header(HttpHeaders.CONTENT_TYPE, "text/markdown;charset=UTF-8")
                .body(closeoutService.getCloseout().markdownReport());
    }
}
