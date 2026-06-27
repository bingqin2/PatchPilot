package io.patchpilot.backend.demo.service;

import io.patchpilot.backend.demo.domain.DemoHandoffShareDeliveryReceiptVo;

import java.util.List;
import java.util.Optional;

public interface DemoHandoffShareDeliveryReceiptRepository {

    DemoHandoffShareDeliveryReceiptVo save(DemoHandoffShareDeliveryReceiptVo receipt);

    List<DemoHandoffShareDeliveryReceiptVo> listRecentReceipts(int limit);

    Optional<DemoHandoffShareDeliveryReceiptVo> findById(String receiptId);
}
