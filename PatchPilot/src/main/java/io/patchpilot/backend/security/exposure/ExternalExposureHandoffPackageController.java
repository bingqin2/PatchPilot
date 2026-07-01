package io.patchpilot.backend.security.exposure;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureHandoffPackageVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/security/external-exposure-handoff-package")
@RequiredArgsConstructor
public class ExternalExposureHandoffPackageController {

    private final ExternalExposureHandoffPackageService handoffPackageService;

    @GetMapping
    public ApiResponse<ExternalExposureHandoffPackageVo> getExternalExposureHandoffPackage() {
        return ApiResponse.ok(handoffPackageService.getHandoffPackage());
    }

    @GetMapping(value = "/report/download", produces = "text/markdown;charset=UTF-8")
    public ResponseEntity<String> downloadExternalExposureHandoffPackageReport() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"patchpilot-external-exposure-handoff-package.md\"")
                .header(HttpHeaders.CONTENT_TYPE, "text/markdown;charset=UTF-8")
                .body(handoffPackageService.getHandoffPackage().markdownReport());
    }
}
