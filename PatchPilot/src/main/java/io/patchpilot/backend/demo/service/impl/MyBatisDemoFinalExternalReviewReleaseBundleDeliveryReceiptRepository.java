package io.patchpilot.backend.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.demo.convert.DemoFinalExternalReviewReleaseBundleDeliveryReceiptConvert;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.entity.DemoFinalExternalReviewReleaseBundleDeliveryReceiptEntity;
import io.patchpilot.backend.demo.mapper.DemoFinalExternalReviewReleaseBundleDeliveryReceiptMapper;
import io.patchpilot.backend.demo.service.DemoFinalExternalReviewReleaseBundleDeliveryReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisDemoFinalExternalReviewReleaseBundleDeliveryReceiptRepository
        implements DemoFinalExternalReviewReleaseBundleDeliveryReceiptRepository {

    private final DemoFinalExternalReviewReleaseBundleDeliveryReceiptMapper receiptMapper;

    @Override
    public DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo save(
            DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo receipt
    ) {
        receiptMapper.insert(DemoFinalExternalReviewReleaseBundleDeliveryReceiptConvert.toEntity(receipt));
        return receipt;
    }

    @Override
    public List<DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo> listRecentReceipts(int limit) {
        LambdaQueryWrapper<DemoFinalExternalReviewReleaseBundleDeliveryReceiptEntity> queryWrapper =
                new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(DemoFinalExternalReviewReleaseBundleDeliveryReceiptEntity::getCreatedAt)
                .last("LIMIT " + limit);
        return receiptMapper.selectList(queryWrapper).stream()
                .map(DemoFinalExternalReviewReleaseBundleDeliveryReceiptConvert::toVo)
                .toList();
    }

    @Override
    public Optional<DemoFinalExternalReviewReleaseBundleDeliveryReceiptVo> findById(String receiptId) {
        return Optional.ofNullable(receiptMapper.selectById(receiptId))
                .map(DemoFinalExternalReviewReleaseBundleDeliveryReceiptConvert::toVo);
    }
}
