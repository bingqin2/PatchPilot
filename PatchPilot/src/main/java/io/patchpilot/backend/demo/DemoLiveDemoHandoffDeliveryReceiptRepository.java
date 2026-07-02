package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoHandoffDeliveryReceiptVo;

import java.util.List;
import java.util.Optional;

public interface DemoLiveDemoHandoffDeliveryReceiptRepository {

    DemoLiveDemoHandoffDeliveryReceiptVo save(DemoLiveDemoHandoffDeliveryReceiptVo receipt);

    List<DemoLiveDemoHandoffDeliveryReceiptVo> listRecentReceipts(int limit);

    Optional<DemoLiveDemoHandoffDeliveryReceiptVo> findById(String receiptId);
}
