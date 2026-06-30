package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo;

import java.util.List;
import java.util.Optional;

public interface DemoFinalExternalReviewReleaseBundleDeliveryReceiptRepository {

    DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo save(
            DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo receipt
    );

    List<DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo> listRecentReceipts(int limit);

    Optional<DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo> findById(String receiptId);
}
