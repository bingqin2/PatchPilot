package io.patchpilot.backend.demo;

import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.demo.domain.DemoLiveDemoHandoffDeliveryFinalizationArchiveVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoHandoffDeliveryFinalizationVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoHandoffDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.DemoLiveDemoHandoffPackageVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/demo/live-demo-handoff-package")
@RequiredArgsConstructor
public class DemoLiveDemoHandoffPackageController {

    private final DemoLiveDemoHandoffPackageService service;
    private final DemoLiveDemoHandoffDeliveryReceiptService receiptService;
    private final DemoLiveDemoHandoffDeliveryFinalizationService finalizationService;
    private final DemoLiveDemoHandoffDeliveryFinalizationArchiveService finalizationArchiveService;

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

    @PostMapping("/delivery-receipts")
    public ApiResponse<DemoLiveDemoHandoffDeliveryReceiptVo> recordDeliveryReceipt(
            @RequestBody DemoLiveDemoHandoffDeliveryReceiptRequestDto request
    ) {
        return ApiResponse.ok(receiptService.recordDeliveryReceipt(request));
    }

    @GetMapping("/delivery-receipts")
    public ApiResponse<List<DemoLiveDemoHandoffDeliveryReceiptVo>> listDeliveryReceipts() {
        return ApiResponse.ok(receiptService.listRecentReceipts());
    }

    @GetMapping("/delivery-receipts/{receiptId}/report/download")
    public ResponseEntity<byte[]> downloadDeliveryReceiptReport(@PathVariable String receiptId) {
        return receiptService.findReceipt(receiptId)
                .map(receipt -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("text/markdown;charset=UTF-8"))
                        .header(
                                HttpHeaders.CONTENT_DISPOSITION,
                                ContentDisposition.attachment()
                                        .filename(
                                                "patchpilot-live-demo-handoff-delivery-receipt-"
                                                        + receipt.id()
                                                        + ".md",
                                                StandardCharsets.UTF_8
                                        )
                                        .build()
                                        .toString()
                        )
                        .body(receipt.markdownReport().getBytes(StandardCharsets.UTF_8)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/delivery-finalization")
    public ApiResponse<DemoLiveDemoHandoffDeliveryFinalizationVo> getDeliveryFinalization() {
        return ApiResponse.ok(finalizationService.getFinalizationGate());
    }

    @GetMapping("/delivery-finalization/report/download")
    public ResponseEntity<byte[]> downloadDeliveryFinalizationReport() {
        byte[] body = finalizationService
                .getFinalizationGate()
                .markdownReport()
                .getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/markdown;charset=UTF-8"))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(
                                        "patchpilot-live-demo-handoff-delivery-finalization.md",
                                        StandardCharsets.UTF_8
                                )
                                .build()
                                .toString()
                )
                .body(body);
    }

    @PostMapping("/delivery-finalization/archives")
    public ApiResponse<DemoLiveDemoHandoffDeliveryFinalizationArchiveVo> archiveDeliveryFinalization() {
        return ApiResponse.ok(finalizationArchiveService.archiveFinalization());
    }

    @GetMapping("/delivery-finalization/archives")
    public ApiResponse<List<DemoLiveDemoHandoffDeliveryFinalizationArchiveVo>> listDeliveryFinalizationArchives() {
        return ApiResponse.ok(finalizationArchiveService.listRecentArchives());
    }

    @GetMapping("/delivery-finalization/archives/{archiveId}/report/download")
    public ResponseEntity<byte[]> downloadDeliveryFinalizationArchiveReport(@PathVariable String archiveId) {
        return finalizationArchiveService.findArchive(archiveId)
                .map(archive -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("text/markdown;charset=UTF-8"))
                        .header(
                                HttpHeaders.CONTENT_DISPOSITION,
                                ContentDisposition.attachment()
                                        .filename(
                                                "patchpilot-live-demo-handoff-delivery-finalization-archive-"
                                                        + archive.id()
                                                        + ".md",
                                                StandardCharsets.UTF_8
                                        )
                                        .build()
                                        .toString()
                        )
                        .body(archive.report().getBytes(StandardCharsets.UTF_8)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
