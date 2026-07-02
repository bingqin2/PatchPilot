package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLiveDemoHandoffDeliveryReceiptVo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryDemoLiveDemoHandoffDeliveryReceiptRepository
        implements DemoLiveDemoHandoffDeliveryReceiptRepository {

    private static final int MAX_RECEIPTS = 20;

    private final List<DemoLiveDemoHandoffDeliveryReceiptVo> receipts = new CopyOnWriteArrayList<>();

    @Override
    public DemoLiveDemoHandoffDeliveryReceiptVo save(DemoLiveDemoHandoffDeliveryReceiptVo receipt) {
        receipts.add(0, receipt);
        trimReceipts();
        return receipt;
    }

    @Override
    public List<DemoLiveDemoHandoffDeliveryReceiptVo> listRecentReceipts(int limit) {
        return receipts.stream()
                .limit(limit)
                .toList();
    }

    @Override
    public Optional<DemoLiveDemoHandoffDeliveryReceiptVo> findById(String receiptId) {
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
