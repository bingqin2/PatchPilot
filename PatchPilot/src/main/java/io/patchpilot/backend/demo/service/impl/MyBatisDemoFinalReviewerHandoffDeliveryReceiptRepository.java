package io.patchpilot.backend.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.demo.convert.DemoFinalReviewerHandoffDeliveryReceiptConvert;
import io.patchpilot.backend.demo.domain.DemoFinalReviewerHandoffDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.entity.DemoFinalReviewerHandoffDeliveryReceiptEntity;
import io.patchpilot.backend.demo.mapper.DemoFinalReviewerHandoffDeliveryReceiptMapper;
import io.patchpilot.backend.demo.service.DemoFinalReviewerHandoffDeliveryReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisDemoFinalReviewerHandoffDeliveryReceiptRepository
        implements DemoFinalReviewerHandoffDeliveryReceiptRepository {

    private final DemoFinalReviewerHandoffDeliveryReceiptMapper receiptMapper;

    @Override
    public DemoFinalReviewerHandoffDeliveryReceiptVo save(
            DemoFinalReviewerHandoffDeliveryReceiptVo receipt
    ) {
        receiptMapper.insert(DemoFinalReviewerHandoffDeliveryReceiptConvert.toEntity(receipt));
        return receipt;
    }

    @Override
    public List<DemoFinalReviewerHandoffDeliveryReceiptVo> listRecentReceipts(int limit) {
        LambdaQueryWrapper<DemoFinalReviewerHandoffDeliveryReceiptEntity> queryWrapper =
                new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(DemoFinalReviewerHandoffDeliveryReceiptEntity::getCreatedAt)
                .last("LIMIT " + limit);
        return receiptMapper.selectList(queryWrapper).stream()
                .map(DemoFinalReviewerHandoffDeliveryReceiptConvert::toVo)
                .toList();
    }

    @Override
    public Optional<DemoFinalReviewerHandoffDeliveryReceiptVo> findById(String receiptId) {
        return Optional.ofNullable(receiptMapper.selectById(receiptId))
                .map(DemoFinalReviewerHandoffDeliveryReceiptConvert::toVo);
    }
}
