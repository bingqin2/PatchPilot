package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptVo;

import java.util.List;
import java.util.Optional;

public interface DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptRepository {

    DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptVo save(
            DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptVo receipt
    );

    List<DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptVo> listRecentReceipts(int limit);

    Optional<DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptVo> findById(String receiptId);
}
