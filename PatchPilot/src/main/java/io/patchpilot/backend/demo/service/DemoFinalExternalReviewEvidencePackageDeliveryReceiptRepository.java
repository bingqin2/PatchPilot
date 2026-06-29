package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo;

import java.util.List;
import java.util.Optional;

public interface DemoFinalExternalReviewEvidencePackageDeliveryReceiptRepository {

    DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo save(
            DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo receipt
    );

    List<DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo> listRecentReceipts(int limit);

    Optional<DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo> findById(String receiptId);
}
