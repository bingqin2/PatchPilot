package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceShareDeliveryReceiptVo;

import java.util.List;
import java.util.Optional;

public interface DemoFinalAcceptanceShareDeliveryReceiptRepository {

    DemoFinalAcceptanceShareDeliveryReceiptVo save(DemoFinalAcceptanceShareDeliveryReceiptVo receipt);

    List<DemoFinalAcceptanceShareDeliveryReceiptVo> listRecentReceipts(int limit);

    Optional<DemoFinalAcceptanceShareDeliveryReceiptVo> findById(String receiptId);
}
