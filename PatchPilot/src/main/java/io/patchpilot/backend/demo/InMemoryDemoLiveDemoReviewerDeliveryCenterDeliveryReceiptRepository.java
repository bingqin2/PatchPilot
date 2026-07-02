package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptVo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryDemoLiveDemoReviewerDeliveryCenterDeliveryReceiptRepository
        implements DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptRepository {

    private static final int MAX_STORED_RECEIPTS = 20;

    private final List<DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptVo> receipts =
            new CopyOnWriteArrayList<>();

    @Override
    public DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptVo save(
            DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptVo receipt
    ) {
        receipts.add(0, receipt);
        if (receipts.size() > MAX_STORED_RECEIPTS) {
            receipts.remove(receipts.size() - 1);
        }
        return receipt;
    }

    @Override
    public List<DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptVo> listRecentReceipts(int limit) {
        return receipts.stream().limit(Math.max(0, limit)).toList();
    }

    @Override
    public Optional<DemoLiveDemoReviewerDeliveryCenterDeliveryReceiptVo> findById(String receiptId) {
        return receipts.stream()
                .filter(receipt -> receipt.id().equals(receiptId))
                .findFirst();
    }
}
