package io.patchpilot.backend.demo.service.impl;

import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo;
import io.patchpilot.backend.demo.service.DemoFinalExternalReviewEvidencePackageDeliveryReceiptRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
@Profile("default")
public class InMemoryDemoFinalExternalReviewEvidencePackageDeliveryReceiptRepository
        implements DemoFinalExternalReviewEvidencePackageDeliveryReceiptRepository {

    private static final int MAX_RECEIPTS = 20;

    private final List<DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo> receipts =
            new CopyOnWriteArrayList<>();

    @Override
    public DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo save(
            DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo receipt
    ) {
        receipts.add(0, receipt);
        trimReceipts();
        return receipt;
    }

    @Override
    public List<DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo> listRecentReceipts(int limit) {
        return receipts.stream()
                .limit(limit)
                .toList();
    }

    @Override
    public Optional<DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo> findById(String receiptId) {
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
