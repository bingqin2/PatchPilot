package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageShareDeliveryReceiptVo;

import java.util.List;
import java.util.Optional;

public interface FixTaskEvidencePackageShareDeliveryReceiptRepository {

    FixTaskEvidencePackageShareDeliveryReceiptVo save(FixTaskEvidencePackageShareDeliveryReceiptVo receipt);

    List<FixTaskEvidencePackageShareDeliveryReceiptVo> listRecentReceipts(int limit);

    Optional<FixTaskEvidencePackageShareDeliveryReceiptVo> findById(String receiptId);
}
