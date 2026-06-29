package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo;

import java.util.List;
import java.util.Optional;

public interface DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptRepository {

    DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo save(
            DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo receipt
    );

    List<DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo> listRecentReceipts(int limit);

    Optional<DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo> findById(String receiptId);
}
