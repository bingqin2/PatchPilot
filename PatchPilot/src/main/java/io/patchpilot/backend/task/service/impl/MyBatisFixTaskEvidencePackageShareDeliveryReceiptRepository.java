package io.patchpilot.backend.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.task.convert.FixTaskEvidencePackageShareDeliveryReceiptConvert;
import io.patchpilot.backend.task.domain.entity.FixTaskEvidencePackageShareDeliveryReceiptEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageShareDeliveryReceiptVo;
import io.patchpilot.backend.task.mapper.FixTaskEvidencePackageShareDeliveryReceiptMapper;
import io.patchpilot.backend.task.service.FixTaskEvidencePackageShareDeliveryReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisFixTaskEvidencePackageShareDeliveryReceiptRepository
        implements FixTaskEvidencePackageShareDeliveryReceiptRepository {

    private final FixTaskEvidencePackageShareDeliveryReceiptMapper receiptMapper;

    @Override
    public FixTaskEvidencePackageShareDeliveryReceiptVo save(
            FixTaskEvidencePackageShareDeliveryReceiptVo receipt
    ) {
        receiptMapper.insert(FixTaskEvidencePackageShareDeliveryReceiptConvert.toEntity(receipt));
        return receipt;
    }

    @Override
    public List<FixTaskEvidencePackageShareDeliveryReceiptVo> listRecentReceipts(int limit) {
        LambdaQueryWrapper<FixTaskEvidencePackageShareDeliveryReceiptEntity> queryWrapper =
                new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(FixTaskEvidencePackageShareDeliveryReceiptEntity::getCreatedAt)
                .last("LIMIT " + limit);
        return receiptMapper.selectList(queryWrapper).stream()
                .map(FixTaskEvidencePackageShareDeliveryReceiptConvert::toVo)
                .toList();
    }

    @Override
    public Optional<FixTaskEvidencePackageShareDeliveryReceiptVo> findById(String receiptId) {
        return Optional.ofNullable(receiptMapper.selectById(receiptId))
                .map(FixTaskEvidencePackageShareDeliveryReceiptConvert::toVo);
    }
}
