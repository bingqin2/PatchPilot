package io.patchpilot.backend.demo.service.impl;

import io.patchpilot.backend.demo.domain.DemoFinalReviewerHandoffDeliveryReceiptVo;
import io.patchpilot.backend.demo.service.DemoFinalReviewerHandoffDeliveryReceiptRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
@Profile("default")
public class InMemoryDemoFinalReviewerHandoffDeliveryReceiptRepository
        implements DemoFinalReviewerHandoffDeliveryReceiptRepository {

    private static final int MAX_RECEIPTS = 20;

    private final List<DemoFinalReviewerHandoffDeliveryReceiptVo> receipts =
            new CopyOnWriteArrayList<>();

    @Override
    public DemoFinalReviewerHandoffDeliveryReceiptVo save(
            DemoFinalReviewerHandoffDeliveryReceiptVo receipt
    ) {
        receipts.add(0, receipt);
        trimReceipts();
        return receipt;
    }

    @Override
    public List<DemoFinalReviewerHandoffDeliveryReceiptVo> listRecentReceipts(int limit) {
        return receipts.stream()
                .limit(limit)
                .toList();
    }

    @Override
    public Optional<DemoFinalReviewerHandoffDeliveryReceiptVo> findById(String receiptId) {
        return receipts.stream()
                .filter(receipt -> receipt.id().equals(receiptId))
                .findFirst();
    }

    private void trimReceipts() {
        while (receipts.size() > MAX_RECEIPTS) {
            receipts.remove(receipts.size() - 1);
        }
    }
}
