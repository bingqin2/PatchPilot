package io.patchpilot.backend.task.service.impl;

import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageShareDeliveryReceiptVo;
import io.patchpilot.backend.task.service.FixTaskEvidencePackageShareDeliveryReceiptRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
@Profile("default")
public class InMemoryFixTaskEvidencePackageShareDeliveryReceiptRepository
        implements FixTaskEvidencePackageShareDeliveryReceiptRepository {

    private static final int MAX_RECEIPTS = 20;

    private final List<FixTaskEvidencePackageShareDeliveryReceiptVo> receipts = new CopyOnWriteArrayList<>();

    @Override
    public FixTaskEvidencePackageShareDeliveryReceiptVo save(
            FixTaskEvidencePackageShareDeliveryReceiptVo receipt
    ) {
        receipts.add(0, receipt);
        trimReceipts();
        return receipt;
    }

    @Override
    public List<FixTaskEvidencePackageShareDeliveryReceiptVo> listRecentReceipts(int limit) {
        return receipts.stream()
                .limit(limit)
                .toList();
    }

    @Override
    public Optional<FixTaskEvidencePackageShareDeliveryReceiptVo> findById(String receiptId) {
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
