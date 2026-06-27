package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceShareDeliveryReceiptVo;

import java.util.List;
import java.util.Optional;

public interface DemoLaunchEvidenceShareDeliveryReceiptRepository {

    DemoLaunchEvidenceShareDeliveryReceiptVo save(DemoLaunchEvidenceShareDeliveryReceiptVo receipt);

    List<DemoLaunchEvidenceShareDeliveryReceiptVo> listRecentReceipts(int limit);

    Optional<DemoLaunchEvidenceShareDeliveryReceiptVo> findById(String receiptId);
}
