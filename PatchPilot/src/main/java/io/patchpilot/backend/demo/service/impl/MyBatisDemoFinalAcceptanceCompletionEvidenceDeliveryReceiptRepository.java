package io.patchpilot.backend.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.demo.convert.DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptConvert;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.entity.DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptEntity;
import io.patchpilot.backend.demo.mapper.DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptMapper;
import io.patchpilot.backend.demo.service.DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisDemoFinalAcceptanceCompletionEvidenceDeliveryReceiptRepository
        implements DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptRepository {

    private final DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptMapper receiptMapper;

    @Override
    public DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo save(
            DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo receipt
    ) {
        receiptMapper.insert(DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptConvert.toEntity(receipt));
        return receipt;
    }

    @Override
    public List<DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo> listRecentReceipts(int limit) {
        LambdaQueryWrapper<DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptEntity> queryWrapper =
                new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptEntity::getCreatedAt)
                .last("LIMIT " + limit);
        return receiptMapper.selectList(queryWrapper).stream()
                .map(DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptConvert::toVo)
                .toList();
    }

    @Override
    public Optional<DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptVo> findById(String receiptId) {
        return Optional.ofNullable(receiptMapper.selectById(receiptId))
                .map(DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptConvert::toVo);
    }
}
