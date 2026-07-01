package io.patchpilot.backend.security.exposure;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureOperatorHandoffChecklistVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/security/external-exposure-operator-handoff-checklist")
@RequiredArgsConstructor
public class ExternalExposureOperatorHandoffChecklistController {

    private final ExternalExposureOperatorHandoffChecklistService checklistService;

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
}
