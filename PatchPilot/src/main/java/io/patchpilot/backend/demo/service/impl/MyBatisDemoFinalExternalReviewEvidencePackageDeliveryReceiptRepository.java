package io.patchpilot.backend.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.demo.convert.DemoFinalExternalReviewEvidencePackageDeliveryReceiptConvert;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.entity.DemoFinalExternalReviewEvidencePackageDeliveryReceiptEntity;
import io.patchpilot.backend.demo.mapper.DemoFinalExternalReviewEvidencePackageDeliveryReceiptMapper;
import io.patchpilot.backend.demo.service.DemoFinalExternalReviewEvidencePackageDeliveryReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisDemoFinalExternalReviewEvidencePackageDeliveryReceiptRepository
        implements DemoFinalExternalReviewEvidencePackageDeliveryReceiptRepository {

    private final DemoFinalExternalReviewEvidencePackageDeliveryReceiptMapper receiptMapper;

    @Override
    public DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo save(
            DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo receipt
    ) {
        receiptMapper.insert(DemoFinalExternalReviewEvidencePackageDeliveryReceiptConvert.toEntity(receipt));
        return receipt;
    }

    @Override
    public List<DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo> listRecentReceipts(int limit) {
        LambdaQueryWrapper<DemoFinalExternalReviewEvidencePackageDeliveryReceiptEntity> queryWrapper =
                new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(DemoFinalExternalReviewEvidencePackageDeliveryReceiptEntity::getCreatedAt)
                .last("LIMIT " + limit);
        return receiptMapper.selectList(queryWrapper).stream()
                .map(DemoFinalExternalReviewEvidencePackageDeliveryReceiptConvert::toVo)
                .toList();
    }

    @Override
    public Optional<DemoFinalExternalReviewEvidencePackageDeliveryReceiptVo> findById(String receiptId) {
        return Optional.ofNullable(receiptMapper.selectById(receiptId))
                .map(DemoFinalExternalReviewEvidencePackageDeliveryReceiptConvert::toVo);
    }
}
