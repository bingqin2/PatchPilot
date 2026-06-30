package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalReviewerHandoffDeliveryReceiptVo;

import java.util.List;
import java.util.Optional;

public interface DemoFinalReviewerHandoffDeliveryReceiptRepository {

    DemoFinalReviewerHandoffDeliveryReceiptVo save(
            DemoFinalReviewerHandoffDeliveryReceiptVo receipt
    );

    List<DemoFinalReviewerHandoffDeliveryReceiptVo> listRecentReceipts(int limit);

    Optional<DemoFinalReviewerHandoffDeliveryReceiptVo> findById(String receiptId);
}
